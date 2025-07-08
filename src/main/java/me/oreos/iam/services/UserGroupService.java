package me.oreos.iam.services;


import org.springframework.stereotype.Service;
import org.wakanda.framework.service.BaseService;
import me.oreos.iam.entities.UserGroup;

@Service
public interface UserGroupService extends BaseService<UserGroup, Integer> {
}
