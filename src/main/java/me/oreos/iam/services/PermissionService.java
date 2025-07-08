package me.oreos.iam.services;


import org.springframework.stereotype.Service;
import org.wakanda.framework.service.BaseService;
import me.oreos.iam.entities.Permission;

@Service
public interface PermissionService extends BaseService<Permission, Integer> {
}
