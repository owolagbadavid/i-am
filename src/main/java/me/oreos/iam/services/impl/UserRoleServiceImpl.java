package me.oreos.iam.services.impl;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import me.oreos.iam.entities.UserRole;
import me.oreos.iam.repositories.UserRoleRepository;
import me.oreos.iam.services.UserRoleService;

@Component
@Transactional
@Slf4j
public class UserRoleServiceImpl extends MyBaseServiceImpl<UserRole, Integer> implements UserRoleService {
    private final UserRoleRepository repository;
    protected UserRoleServiceImpl(UserRoleRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Optional<UserRole> findByUserIdAndRoleId(Integer userId, Integer roleId) {
        return Optional.ofNullable(repository.findByUserIdAndRoleId(userId, roleId));
    }
}
