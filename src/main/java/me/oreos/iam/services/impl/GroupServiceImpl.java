package me.oreos.iam.services.impl;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;
import org.wakanda.framework.service.BaseServiceImpl;

import lombok.extern.slf4j.Slf4j;
import me.oreos.iam.entities.Group;
import me.oreos.iam.repositories.GroupRepository;
import me.oreos.iam.services.GroupService;

@Component
@Transactional
@Slf4j
public class GroupServiceImpl extends BaseServiceImpl<Group, Integer> implements GroupService {
    protected GroupServiceImpl(GroupRepository repository) {
        super(repository);
    }
}
