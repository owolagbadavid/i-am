package me.oreos.iam.services;


import java.util.Optional;

import org.springframework.stereotype.Service;
import org.wakanda.framework.service.BaseService;
import me.oreos.iam.entities.User;

@Service
public interface UserService extends BaseService<User, Integer> {
    public Optional<User> findByEmail(String email);

    public Optional<User> findDistinctByEmail(String email);

    public User addUserRole(Integer userId, Integer roleId) throws Exception;
    
    public User addUserPolicy(Integer userId, Integer policyId);
}
