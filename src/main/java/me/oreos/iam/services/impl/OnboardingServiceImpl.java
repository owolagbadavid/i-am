package me.oreos.iam.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.wakanda.framework.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.oreos.iam.Dtos.OnboardAdminDto;
import me.oreos.iam.entities.Action;
import me.oreos.iam.entities.Permission;
import me.oreos.iam.entities.ResourceType;
import me.oreos.iam.entities.Role;
import me.oreos.iam.entities.RolePermission;
import me.oreos.iam.entities.User;
import me.oreos.iam.entities.UserRole;
import me.oreos.iam.entities.enums.EffectiveScopeEnum;
import me.oreos.iam.repositories.*;
import me.oreos.iam.services.MailService;
import me.oreos.iam.services.OnboardingService;
import me.oreos.iam.services.utils.Helper;
import me.oreos.iam.services.utils.PasswordHasher;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OnboardingServiceImpl implements OnboardingService {

    private final RoleRepository roleRepository;
    private final ActionRepository actionRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final ResourceTypeRepository resourceTypeRepository;
    private final MailService mailService;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private String[] defaultResourceTypes() {
        return new String[] { "user", "group", "resource", "role", "policy" };
    }

    private String[] defaultActions() {
        return new String[] { "create", "read", "update", "delete", "list" };
    }

    @Override
    public void onboardAdmin(OnboardAdminDto dto) {
        try {
            // check if user already exists
            if (userRepository.findOne((root, query, cb) -> cb.conjunction()).isPresent()) {
                throw new BaseException(400, "User already exists");
            }

            // create admin user
            User adminUser = new User();
            adminUser.setEmail(dto.getEmail());
            adminUser.setUsername(dto.getUsername());
            adminUser.setPasswordHash(PasswordHasher.hashPassword(dto.getPassword()));

            userRepository.save(adminUser);

            List<ResourceType> resourceTypes = new ArrayList<>();
            List<Action> actions = new ArrayList<>();
            List<Permission> permissions = new ArrayList<>();
            List<RolePermission> rolePermissions = new ArrayList<>();

            // create default resource types
            for (String resourceTypeCode : defaultResourceTypes()) {
                ResourceType resourceType = new ResourceType();
                resourceType.setCode(resourceTypeCode);
                resourceTypes.add(resourceTypeRepository.save(resourceType));
            }

            // create default actions
            for (String actionCode : defaultActions()) {
                Action action = new Action();
                action.setCode(actionCode);
                actions.add(actionRepository.save(action));
            }

            // create default permissions
            for (String resourceTypeCode : defaultResourceTypes()) {
                for (String actionCode : defaultActions()) {
                    Action action = actions.stream()
                            .filter(a -> a.getCode().equals(actionCode))
                            .findFirst()
                            .orElseThrow(() -> new BaseException(404, "Action not found: " + actionCode));
                    ResourceType resourceType = resourceTypes.stream()
                            .filter(r -> r.getCode().equals(resourceTypeCode))
                            .findFirst()
                            .orElseThrow(() -> new BaseException(404, "Resource type not found: " + resourceTypeCode));

                    Permission permission = new Permission();
                    permission.setAction(action);
                    permission.setResourceType(resourceType);
                    permissions.add(permission);
                }
            }

            permissionRepository.saveAll(permissions);

            // create admin role
            Role adminRole = new Role();
            adminRole.setCode("Admin");
            adminRole.setDescription("Administrator role with all permissions");

            roleRepository.save(adminRole);

            // Assign all permissions to the admin role
            for (Permission permission : permissions) {
                var rolePermission = new RolePermission();
                rolePermission.setRole(adminRole);
                rolePermission.setPermission(permission);
                rolePermission.setScope(EffectiveScopeEnum.ALL);
                rolePermissions.add(rolePermission);
            }

            rolePermissionRepository.saveAll(rolePermissions);

            // Assign the admin role to the admin user

            var userRole = new UserRole();
            userRole.setUser(adminUser);
            userRole.setRole(adminRole);

            userRoleRepository.save(userRole);

            if (dto.init) {
                var otp = Helper.generateRandomCode();
                // Store OTP in Redis with a TTL of 5 minutes
                redisTemplate.opsForValue().set("onboard_admin_otp", otp, 5, TimeUnit.MINUTES);

                // Send OTP email
                mailService.to(adminUser.getEmail())
                        .subject("Admin Onboarding OTP")
                        .body("Your OTP for admin onboarding is: " + otp)
                        .send();

                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return; // Rollback the transaction to prevent further processing

            } else {
                var otp = redisTemplate.opsForValue().get("onboard_admin_otp");
                if (otp == null || !otp.equals(dto.getOtp())) {
                    throw new BaseException(401, "Invalid OTP");
                }

                // Clear the OTP after successful onboarding
                redisTemplate.delete("onboard_admin_otp");

                // Send success email
                mailService.to(adminUser.getEmail())
                        .subject("Admin Onboarding Successful")
                        .body("Admin user created successfully with username: " + adminUser.getUsername())
                        .send();
            }
        } catch (BaseException e) {
            log.error("Error during onboarding admin: {}", e.getMessage());
            throw e; // Re-throw the exception to be handled by the controller
        } catch (Exception e) {
            log.error("Error during onboarding admin", e);
            throw new BaseException(500, "Error during onboarding admin: " + e.getMessage());
        }

    }
}