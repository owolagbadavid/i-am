package me.oreos.iam.services;


import org.springframework.stereotype.Service;
import org.wakanda.framework.service.BaseService;
import me.oreos.iam.entities.Group;

@Service
public interface GroupService extends BaseService<Group, Integer> {
}
