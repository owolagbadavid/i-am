package me.oreos.iam.repositories;

import org.springframework.stereotype.Repository;
import org.wakanda.framework.repository.BaseRepository;

import me.oreos.iam.entities.UserPolicy;

@Repository
public interface UserPolicyRepository extends BaseRepository<me.oreos.iam.entities.UserPolicy, Integer> { 
    UserPolicy findByUserIdAndPolicyId(Integer userId, Integer policyId);
}
