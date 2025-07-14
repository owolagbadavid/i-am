package me.oreos.iam.services;


import java.util.Optional;

import org.springframework.stereotype.Service;
import me.oreos.iam.entities.UserGroup;

@Service
public interface UserGroupService extends MyBaseService<UserGroup, Integer> {
    Optional<UserGroup> findByUserIdAndGroupId(Integer userId, Integer groupId);
}
