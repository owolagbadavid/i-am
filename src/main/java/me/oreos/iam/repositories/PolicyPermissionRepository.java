package me.oreos.iam.repositories;

import org.springframework.stereotype.Repository;
import org.wakanda.framework.repository.BaseRepository;

import me.oreos.iam.entities.PolicyPermission;

@Repository
public interface PolicyPermissionRepository extends BaseRepository<me.oreos.iam.entities.PolicyPermission, Integer> {
    PolicyPermission findByPolicyIdAndPermissionId(Integer policyId, Integer permissionId);
}
