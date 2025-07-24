package me.oreos.iam.services.impl;

import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import me.oreos.iam.entities.PolicyPermission;
import me.oreos.iam.repositories.PolicyPermissionRepository;
import me.oreos.iam.services.PolicyPermissionService;

@Component
@Transactional
@Slf4j
public class PolicyPermissionServiceImpl extends MyBaseServiceImpl<PolicyPermission, Integer> implements PolicyPermissionService {
    private final PolicyPermissionRepository repository;
    protected PolicyPermissionServiceImpl(PolicyPermissionRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Optional<PolicyPermission> findByPolicyIdAndPermissionId(Integer policyId, Integer permissionId) {
        return Optional.ofNullable(repository.findByPolicyIdAndPermissionId(policyId, permissionId));
    }
}
