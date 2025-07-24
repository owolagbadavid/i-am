package me.oreos.iam.services.impl;

import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import me.oreos.iam.entities.UserPolicy;
import me.oreos.iam.repositories.UserPolicyRepository;
import me.oreos.iam.services.UserPolicyService;

@Component
@Transactional
@Slf4j
public class UserPolicyServiceImpl extends MyBaseServiceImpl<UserPolicy, Integer> implements UserPolicyService {
    private final UserPolicyRepository repository;
    protected UserPolicyServiceImpl(UserPolicyRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Optional<UserPolicy> findByUserIdAndPolicyId(Integer userId, Integer policyId) {
        return Optional.ofNullable(repository.findByUserIdAndPolicyId(userId, policyId));
    }
}
