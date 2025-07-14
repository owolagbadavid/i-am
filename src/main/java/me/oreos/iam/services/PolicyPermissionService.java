package me.oreos.iam.services;

import java.util.Optional;

import org.wakanda.framework.service.BaseService;

import me.oreos.iam.entities.PolicyPermission;

public interface PolicyPermissionService extends BaseService<PolicyPermission, Integer> {
    Optional<PolicyPermission> findByPolicyIdAndPermissionId(Integer policyId, Integer permissionId);
}