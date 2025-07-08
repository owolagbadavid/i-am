package me.oreos.iam.services;


import org.springframework.stereotype.Service;
import org.wakanda.framework.service.BaseService;
import me.oreos.iam.entities.Role;

@Service
public interface RoleService extends BaseService<Role, Integer> {
}
