package me.oreos.iam.services.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.wakanda.framework.exception.BaseException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.oreos.iam.Dtos.AuthorizationRequestDto;
import me.oreos.iam.Dtos.ForgotPasswordDto;
import me.oreos.iam.Dtos.LoginDto;
import me.oreos.iam.Dtos.OnboardAdminDto;
import me.oreos.iam.Dtos.ResetPasswordDto;
import me.oreos.iam.entities.Action;
import me.oreos.iam.entities.Resource;
import me.oreos.iam.entities.ResourceType;
import me.oreos.iam.entities.Token;
import me.oreos.iam.entities.User;
import me.oreos.iam.entities.UserGroup;
import me.oreos.iam.entities.enums.EffectiveScopeEnum;
import me.oreos.iam.entities.models.ResourcePermissionModel;
import me.oreos.iam.entities.models.UserPermissionModel;
import me.oreos.iam.repositories.ActionRepository;
import me.oreos.iam.repositories.CustomRepository;
import me.oreos.iam.repositories.PermissionRepository;
import me.oreos.iam.repositories.ResourceRepository;
import me.oreos.iam.repositories.ResourceTypeRepository;
import me.oreos.iam.repositories.UserGroupRepository;
import me.oreos.iam.repositories.UserRepository;
import me.oreos.iam.services.AuthService;
import me.oreos.iam.services.MailService;
import me.oreos.iam.services.SecurityService;
import me.oreos.iam.services.TokenProvider;
import me.oreos.iam.services.TokenService;
import me.oreos.iam.services.UserService;
import me.oreos.iam.services.utils.Helper;
import me.oreos.iam.services.utils.PasswordHasher;
import me.oreos.iam.types.PermissionPair;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {
    private final ActionRepository actionRepository;
    private final ResourceRepository resourceRepository;
    private final TokenService tokenService;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final UserGroupRepository userGroupRepository;
    private final ResourceTypeRepository resourceTypeRepository;
    private final CustomRepository customRepository;
    private final UserService userService;
    private final SecurityService securityService;
    private final MailService mailService;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public String loginHandler(LoginDto loginDto, HttpServletRequest request) throws Exception {
        var userOpt = userService.findByEmail(loginDto.emailAddress);

        if (userOpt.isEmpty()) {
            throw new BaseException(401, "Inavlid credentials");
        }

        String ipAddress = Helper.getClientIp(request);
        String deviceInfo = request.getHeader("User-Agent");
        String loginLocation = null;
        try {
            loginLocation = Helper.getGeoLocation(ipAddress).toString();
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        var user = userOpt.get();

        if (!PasswordHasher.verifyPassword(loginDto.password, user.getPasswordHash())) {
            throw new BaseException(401, "Invalid credentials");
        }

        Map<String, Object> additionalClaims = Map.of(
                "ipAddress", ipAddress,
                "email", user.getEmail(),
                "username", user.getUsername());

        var tokenString = tokenProvider.generateToken(user.getId(), additionalClaims);
        var claims = tokenProvider.validateToken(tokenString);

        var token = new Token();
        token.setUser(user);
        token.setToken(tokenString);
        token.setExpiresAt(new DateTime(claims.getExpiration())); // 1 hour expiration
        token.setIpAddress(ipAddress);
        token.setDeviceInfo(deviceInfo);
        token.setLoginLocation(loginLocation);

        token = tokenService.save(token);
        return token.getToken();
    }

    @Override
    public void forgotPassword(ForgotPasswordDto dto) throws Exception {
        var userOpt = userService.findByEmail(dto.emailAddress);

        // Always return success to avoid user enumeration
        if (userOpt.isEmpty()) {
            return;
        }

        var user = userOpt.get();
        var otp = securityService.generateOtp(user.getEmail());

        mailService.to(user.getEmail())
                .subject("Password Reset Request")
                .body("Your OTP for password reset is: " + otp)
                .send();

        // CompletableFuture.runAsync(() -> {
        // try {
        // mailService.send();
        // } catch (Exception e) {
        // log.error("Error sending email", e);
        // }
        // });
    }

    @Override
    public void resetPassword(ResetPasswordDto dto) throws Exception {
        if (!securityService.verifyOtp(dto.emailAddress, dto.otp)) {
            throw new BaseException(401, "Invalid reset OTP");
        }

        var userOpt = userService.findByEmail(dto.emailAddress);

        if (userOpt.isEmpty()) {
            throw new BaseException(404, "User not found");
        }

        var user = userOpt.get();
        user.setPasswordHash(PasswordHasher.hashPassword(dto.newPassword));

        userService.save(user);

        return;
    }

    @Override
    public void authorize(AuthorizationRequestDto request) {
        var pair = getActionAndResourceType(request.action, request.resourceType);
        var action = pair.getLeft();
        var resourceType = pair.getRight();

        Optional<Resource> resourceOpt = resourceRepository.findByResourceIdAndResourceTypeId(request.resourceId,
                resourceType.getId());

        var claims = validateToken(request.authToken);

        // try parse claims.getSubject() as integer
        Integer userId = null;
        try {
            userId = Integer.parseInt(claims.getSubject());
        } catch (NumberFormatException e) {
            throw new BaseException(401, "Invalid token");
        }

        var userPermissionPair = getUserPermissions(userId, action, resourceType);
        var userPermissions = userPermissionPair.getLeft();
        var user = userPermissionPair.getRight();
        var resourcePermissions = getResourcePermissions(resourceOpt);

        // Authorization logic here

        checkPermissions(userPermissions, user, action.getCode(), resourceType.getCode(), resourceOpt,
                resourcePermissions);
    }

    private Pair<Action, ResourceType> getActionAndResourceType(String actionCode, String resourceTypeCode) {
        var actionOpt = actionRepository.findByCode(actionCode);
        if (actionOpt.isEmpty()) {
            throw new BaseException(404, "Action not found: " + actionCode);
        }

        var resourceTypeOpt = resourceTypeRepository.findByCode(resourceTypeCode);
        if (resourceTypeOpt.isEmpty()) {
            throw new BaseException(404, "Resource type not found: " + resourceTypeCode);
        }

        var action = actionOpt.get();
        var resourceType = resourceTypeOpt.get();

        return Pair.of(action, resourceType);
    }

    private Claims validateToken(String token) {
        var tokenOpt = tokenService.findByToken(token);
        if (tokenOpt.isEmpty()) {
            throw new BaseException(401, "Invalid token");
        }

        var claims = tokenProvider.validateToken(token);
        var isExpired = tokenProvider.isTokenExpired(claims);

        if (isExpired) {
            throw new BaseException(401, "Invalid token");
        }

        if (tokenOpt.get().getExpiresAt().isBefore(new Instant().getMillis())) {
            throw new BaseException(401, "Invalid token");
        }
        return claims;
    }

    private Pair<List<UserPermissionModel>, User> getUserPermissions(Integer userId, Action action,
            ResourceType resourceType) {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new BaseException(404, "User not found");
        }

        var user = userOpt.get();

        var permissionsMap = permissionRepository.findUserPermissions(user.getId(), action.getId(),
                resourceType.getId());

        Gson gson = new Gson();
        System.out.println(gson.toJson(permissionsMap));

        List<UserPermissionModel> permissions = Helper.mapToModel(permissionsMap,
                new TypeReference<List<UserPermissionModel>>() {
                });

        return Pair.of(permissions, user);
    }

    private List<ResourcePermissionModel> getResourcePermissions(Optional<Resource> resourceOpt) {
        if (resourceOpt.isEmpty()) {
            return Collections.emptyList();
        }

        var resourceId = resourceOpt.get().getId();
        var permissionsMap = permissionRepository.findResourcePermissions(resourceId);

        Gson gson = new Gson();
        System.out.println("Resource Permissions: ");
        System.out.println(gson.toJson(permissionsMap));

        List<ResourcePermissionModel> permissions = Helper.mapToModel(permissionsMap,
                new TypeReference<List<ResourcePermissionModel>>() {
                });

        return permissions;
    }

    private void checkPermissions(List<UserPermissionModel> userPermissions, User user, String actionCode,
            String resourceTypeCode, Optional<Resource> resourceOpt,
            List<ResourcePermissionModel> resourcePermissions) throws BaseException {
        for (UserPermissionModel userPermission : userPermissions) {
            // Check if action and resource type match
            if (isActionAndResourceTypeValid(userPermission, actionCode, resourceTypeCode)) {
                // Check scope requirements
                if (checkPermissionScope(userPermission, user, resourceOpt)) {
                    checkResourcePermissions(user, resourcePermissions);
                    return; // Access granted
                }
            }
        }

        throw new BaseException(403,
                "Access denied for action: " + actionCode + " on resource type: " + resourceTypeCode
                        + (resourceOpt.isEmpty()
                                ? ""
                                : " with resource ID: " + resourceOpt.get().getResourceId()));

    }

    private boolean isActionAndResourceTypeValid(UserPermissionModel userPermission, String actionCode,
            String resourceTypeCode) {
        return userPermission.getAction().equals(actionCode)
                && userPermission.getResourceType().equals(resourceTypeCode);
    }

    private boolean checkPermissionScope(UserPermissionModel userPermission, User user, Optional<Resource> resourceOpt)
            throws BaseException {
        EffectiveScopeEnum scope = userPermission.getEffectiveScope();

        if (resourceOpt.isEmpty()) {
            // If no resource is provided, only DEFAULT and ALL scopes are valid
            return scope == EffectiveScopeEnum.DEFAULT || scope == EffectiveScopeEnum.ALL;
        }
        Resource resource = resourceOpt.get();

        switch (scope) {
            case DEFAULT:
            case ALL:
                return true; // DEFAULT and ALL grant access without further checks
            case OWN:
                return checkOwnScope(user, resource);
            case ITEM:
                return checkItemScope(userPermission, resource);
            case GROUP:
                return checkGroupScope(user, resource);
            default:
                throw new BaseException(500, "Unsupported permission scope: " + scope);
        }
    }

    private boolean checkOwnScope(User user, Resource resource) {
        // Check if the resource belongs to the user
        return resource.getUser().getId() != null
                && resource.getUser().getId().equals(user.getId());
    }

    private boolean checkItemScope(UserPermissionModel userPermission, Resource resource) {
        // Check if the resourceId in the permission matches the resource's resourceId
        return userPermission.getResourceId() != null
                && userPermission.getResourceId().equals(resource.getResourceId());
    }

    private boolean checkGroupScope(User user, Resource resource) {
        // Check if the resource's group is one of the user's groups
        Integer groupId = resource.getGroup().getId();
        if (groupId == null) {
            return false;
        }

        UserGroup userGroup = userGroupRepository.findByUserIdAndGroupId(user.getId(), groupId);
        return userGroup != null;
    }

    private void checkResourcePermissions(User user, List<ResourcePermissionModel> resourcePermissions)
            throws BaseException {
        if (resourcePermissions == null || resourcePermissions.isEmpty()) {
            return; // No resource-specific permissions to check
        }

        // Collect all unique (actionId, resourceTypeId) pairs
        List<PermissionPair> requiredPairs = resourcePermissions.stream()
                .map(rp -> new PermissionPair(rp.getActionId(), rp.getResourceTypeId()))
                .distinct()
                .collect(Collectors.toList());

        // Fetch all user permissions for the required pairs
        var userPerms = customRepository.findUserPermissions(user.getId(), requiredPairs);

        // Check if all required resource permissions are satisfied
        for (ResourcePermissionModel resourcePermission : resourcePermissions) {
            boolean hasPermission = false;
            for (UserPermissionModel perm : userPerms) {
                if (perm.getActionId().equals(resourcePermission.getActionId())
                        && perm.getResourceTypeId().equals(resourcePermission.getResourceTypeId())) {
                    hasPermission = true;
                    break;
                }
            }

            if (!hasPermission) {
                throw new BaseException(403,
                        "Missing required permission for action " + resourcePermission.getAction()
                                + " on resource type: " + resourcePermission.getResourceType());
            }
        }
    }

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

            throw new BaseException(500, "Error during onboarding admin");

        } catch (Exception e) {
            log.error("Error during onboarding admin", e);
            throw new BaseException(500, "Error during onboarding admin: " + e.getMessage());
        }

    }
}