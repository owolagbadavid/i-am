package me.oreos.iam.repositories;

import org.springframework.stereotype.Repository;
import org.wakanda.framework.repository.BaseRepository;

import me.oreos.iam.entities.UserGroup;

@Repository
public interface UserGroupRepository extends BaseRepository<me.oreos.iam.entities.UserGroup, Integer> { 
    UserGroup findByUserIdAndGroupId(Integer userId, Integer groupId);
}
