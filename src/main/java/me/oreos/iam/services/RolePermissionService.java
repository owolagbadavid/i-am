package me.oreos.iam.services;

import java.util.Optional;

import org.wakanda.framework.service.BaseService;

import me.oreos.iam.entities.RolePermission;

public interface RolePermissionService extends BaseService<RolePermission, Integer> {

    Optional<RolePermission> findByRoleIdAndPermissionId(Integer roleId, Integer permissionId);

}