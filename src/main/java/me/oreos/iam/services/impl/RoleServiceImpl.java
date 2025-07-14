package me.oreos.iam.services.impl;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;
import org.wakanda.framework.exception.BaseException;
import org.wakanda.framework.service.BaseServiceImpl;

import lombok.extern.slf4j.Slf4j;
import me.oreos.iam.entities.Role;
import me.oreos.iam.entities.RolePermission;
import me.oreos.iam.entities.enums.EffectiveScopeEnum;
import me.oreos.iam.repositories.PermissionRepository;
import me.oreos.iam.repositories.RoleRepository;
import me.oreos.iam.services.RolePermissionService;
import me.oreos.iam.services.RoleService;

@Component
@Transactional
@Slf4j
public class RoleServiceImpl extends BaseServiceImpl<Role, Integer> implements RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionService rolePermissionService;

    protected RoleServiceImpl(RoleRepository repository, PermissionRepository permissionRepository,
            RolePermissionService rolePermissionService) {
        super(repository);
        this.roleRepository = repository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionService = rolePermissionService;
    }

    @Override
    public Role addRolePermission(Integer roleId, Integer permissionId, EffectiveScopeEnum effectiveScope) {
        var roleOpt = roleRepository.findById(roleId);
        if (roleOpt.isEmpty()) {
            throw new BaseException(404, "Role not found");
        }

        var permissionOpt = permissionRepository.findById(permissionId);
        if (permissionOpt.isEmpty()) {
            throw new BaseException(404, "Permission not found");
        }

        var role = roleOpt.get();
        var permission = permissionOpt.get();

        var alreadyExists = rolePermissionService.findByRoleIdAndPermissionId(roleId, permissionId);
        if (alreadyExists.isPresent()) {
            throw new BaseException(409, "Permission already exists in the role");
        }

        var rolePermission = new RolePermission();
        rolePermission.setRole(role);
        rolePermission.setPermission(permission);
        rolePermission.setScope(effectiveScope);

        rolePermissionService.save(rolePermission);
        return role;
    }
}
