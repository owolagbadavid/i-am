package me.oreos.iam.services.impl;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;
import org.wakanda.framework.exception.BaseException;
import org.wakanda.framework.service.BaseServiceImpl;

import lombok.extern.slf4j.Slf4j;
import me.oreos.iam.entities.Policy;
import me.oreos.iam.entities.PolicyPermission;
import me.oreos.iam.entities.enums.EffectiveScopeEnum;
import me.oreos.iam.repositories.PermissionRepository;
import me.oreos.iam.repositories.PolicyRepository;
import me.oreos.iam.services.PolicyPermissionService;
import me.oreos.iam.services.PolicyService;

@Component
@Transactional
@Slf4j
public class PolicyServiceImpl extends BaseServiceImpl<Policy, Integer> implements PolicyService {
    private final PolicyRepository policyRepository;
    private final PermissionRepository permissionRepository;
    private final PolicyPermissionService policyPermissionService;

    protected PolicyServiceImpl(PolicyRepository repository, PolicyPermissionService policyPermissionService,
            PermissionRepository permissionRepository) {
        super(repository);
        this.policyRepository = repository;
        this.policyPermissionService = policyPermissionService;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Policy addPolicyPermission(Integer policyId, Integer permissionId, EffectiveScopeEnum effectiveScope) {
        var policyOpt = policyRepository.findById(policyId);
        if (policyOpt.isEmpty()) {
            throw new BaseException(404, "Policy not found");
        }

        var permissionOpt = permissionRepository.findById(permissionId);
        if (permissionOpt.isEmpty()) {
            throw new BaseException(404, "Permission not found");
        }

        var policy = policyOpt.get();
        var permission = permissionOpt.get();

        var alreadyExists = policyPermissionService.findByPolicyIdAndPermissionId(policyId, permissionId);
        if (alreadyExists.isPresent()) {
            throw new BaseException(409, "Permission already exists in the policy");
        }

        var policyPermission = new PolicyPermission();
        policyPermission.setPolicy(policy);
        policyPermission.setPermission(permission);
        policyPermission.setScope(effectiveScope);

        policyPermissionService.save(policyPermission);
        return policy;
    }
}
