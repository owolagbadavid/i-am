package me.oreos.iam.services.impl;

import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Component;
import org.wakanda.framework.exception.BaseException;

import lombok.extern.slf4j.Slf4j;
import me.oreos.iam.entities.User;
import me.oreos.iam.repositories.PolicyRepository;
import me.oreos.iam.repositories.RoleRepository;
import me.oreos.iam.repositories.UserRepository;
import me.oreos.iam.services.UserPolicyService;
import me.oreos.iam.services.UserRoleService;
import me.oreos.iam.services.UserService;

@Component
@Transactional
@Slf4j
public class UserServiceImpl extends MyBaseServiceImpl<User, Integer> implements UserService {
    private final UserRepository userRepository;
    private final UserRoleService userRoleService;
    private final RoleRepository roleRepository;
    private final UserPolicyService userPolicyService;
    private final PolicyRepository policyRepository;

    protected UserServiceImpl(UserRepository userRepository, UserRoleService userRoleService,
            RoleRepository roleRepository, UserPolicyService userPolicyService, PolicyRepository policyRepository) {
        super(userRepository);
        this.userRepository = userRepository;
        this.userRoleService = userRoleService;
        this.roleRepository = roleRepository;
        this.userPolicyService = userPolicyService;
        this.policyRepository = policyRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        // log.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findDistinctByEmail(String email) {
        // log.debug("Finding distinct user by email: {}", email);
        return userRepository.findDistinctByEmail(email);
    }

    @Override
    public User addUserRole(Integer userId, Integer roleId) {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new BaseException(404, "User not found");
        }

        var roleOpt = roleRepository.findById(roleId);
        if (roleOpt.isEmpty()) {
            throw new BaseException(404, "Role not found");
        }

        var user = userOpt.get();
        var role = roleOpt.get();

        var alreadyExists = userRoleService.findByUserIdAndRoleId(userId, roleId);
        if (alreadyExists.isPresent()) {
            throw new BaseException(409, "User already has this role");
        }
        var userRole = new me.oreos.iam.entities.UserRole();
        userRole.setUser(user);
        userRole.setRole(role);

        userRoleService.save(userRole);
        return user;
    }

    @Override
    public User addUserPolicy(Integer userId, Integer policyId) {
        var userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new BaseException(404, "User not found");
        }

        var policyOpt = policyRepository.findById(policyId);
        if (policyOpt.isEmpty()) {
            throw new BaseException(404, "Policy not found");
        }

        var user = userOpt.get();
        var policy = policyOpt.get();

        var alreadyExists = userPolicyService.findByUserIdAndPolicyId(userId, policyId);
        if (alreadyExists.isPresent()) {
            throw new BaseException(409, "User already has this policy");
        }
        var userPolicy = new me.oreos.iam.entities.UserPolicy();
        userPolicy.setUser(user);
        userPolicy.setPolicy(policy);

        userPolicyService.save(userPolicy);
        return user;
    }

}
