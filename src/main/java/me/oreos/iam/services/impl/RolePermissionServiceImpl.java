package me.oreos.iam.services.impl;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import me.oreos.iam.entities.RolePermission;
import me.oreos.iam.repositories.RolePermissionRepository;
import me.oreos.iam.services.RolePermissionService;

@Component
@Transactional
@Slf4j
public class RolePermissionServiceImpl extends MyBaseServiceImpl<RolePermission, Integer>
        implements RolePermissionService {
    private final RolePermissionRepository repository;

    protected RolePermissionServiceImpl(RolePermissionRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Optional<RolePermission> findByRoleIdAndPermissionId(Integer roleId, Integer permissionId) {
        return Optional.ofNullable(repository.findByRoleIdAndPermissionId(roleId, permissionId));
    }
}
