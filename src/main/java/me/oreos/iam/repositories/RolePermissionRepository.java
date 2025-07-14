package me.oreos.iam.repositories;

import org.springframework.stereotype.Repository;
import org.wakanda.framework.repository.BaseRepository;

import me.oreos.iam.entities.RolePermission;

@Repository
public interface RolePermissionRepository extends BaseRepository<me.oreos.iam.entities.RolePermission, Integer> { 
    RolePermission findByRoleIdAndPermissionId(Integer roleId, Integer permissionId);
}
