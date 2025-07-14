package me.oreos.iam.services;

import org.springframework.stereotype.Service;
import org.wakanda.framework.service.BaseService;
import me.oreos.iam.entities.Role;
import me.oreos.iam.entities.enums.EffectiveScopeEnum;

@Service
public interface RoleService extends BaseService<Role, Integer> {
    public Role addRolePermission(Integer roleId, Integer permissionId, EffectiveScopeEnum effectiveScope);
}
