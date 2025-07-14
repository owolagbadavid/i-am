package me.oreos.iam.services;


import java.util.Optional;

import org.springframework.stereotype.Service;

import me.oreos.iam.entities.UserPolicy;

@Service
public interface UserPolicyService extends MyBaseService<UserPolicy, Integer> {
    Optional<UserPolicy> findByUserIdAndPolicyId(Integer userId, Integer policyId);
}
