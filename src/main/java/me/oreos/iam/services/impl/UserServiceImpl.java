package me.oreos.iam.services.impl;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;
import org.wakanda.framework.exception.BaseException;

import lombok.extern.slf4j.Slf4j;
import me.oreos.iam.entities.User;
import me.oreos.iam.repositories.RoleRepository;
import me.oreos.iam.repositories.UserRepository;
import me.oreos.iam.services.UserRoleService;
import me.oreos.iam.services.UserService;

@Component
@Transactional
@Slf4j
public class UserServiceImpl extends MyBaseServiceImpl<User, Integer> implements UserService {
    private final UserRepository userRepository;
    private final UserRoleService userRoleService;
    private final RoleRepository roleRepository;

    protected UserServiceImpl(UserRepository userRepository, UserRoleService userRoleService,
            RoleRepository roleRepository) {
        super(userRepository);
        this.userRepository = userRepository;
        this.userRoleService = userRoleService;
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        // log.debug("Finding user by email: {}", email);
        return Optional.ofNullable(userRepository.findByEmail(email));
    }

    @Override
    public Optional<User> findDistinctByEmail(String email) {
        // log.debug("Finding distinct user by email: {}", email);
        return Optional.ofNullable(userRepository.findDistinctByEmail(email));
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

}
