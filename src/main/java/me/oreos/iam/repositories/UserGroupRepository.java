package me.oreos.iam.repositories;

import org.springframework.stereotype.Repository;
import org.wakanda.framework.repository.BaseRepository;

@Repository
public interface UserGroupRepository extends BaseRepository<me.oreos.iam.entities.UserGroup, Integer> { 
}
