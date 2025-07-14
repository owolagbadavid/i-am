package me.oreos.iam.services;


import java.util.Optional;

import org.springframework.stereotype.Service;
import me.oreos.iam.entities.UserRole;

@Service
public interface UserRoleService extends MyBaseService<UserRole, Integer> {
    Optional<UserRole> findByUserIdAndRoleId(Integer userId, Integer roleId);
}
