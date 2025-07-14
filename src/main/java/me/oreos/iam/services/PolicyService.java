package me.oreos.iam.services;

import org.springframework.stereotype.Service;
import org.wakanda.framework.service.BaseService;
import me.oreos.iam.entities.Policy;
import me.oreos.iam.entities.enums.EffectiveScopeEnum;

@Service
public interface PolicyService extends BaseService<Policy, Integer> {
    Policy addPolicyPermission(Integer policyId, Integer permissionId, EffectiveScopeEnum effectiveScope);
}
