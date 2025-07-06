package me.oreos.iam.repositories;

import org.springframework.stereotype.Repository;
import org.wakanda.framework.repository.BaseRepository;

@Repository
public interface UserPolicyRepository extends BaseRepository<me.oreos.iam.entities.UserPolicy, Integer> { 
}
