package me.oreos.iam.services.impl;

import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import me.oreos.iam.entities.UserGroup;
import me.oreos.iam.repositories.UserGroupRepository;
import me.oreos.iam.services.UserGroupService;

@Component
@Transactional
@Slf4j
public class UserGroupServiceImpl extends MyBaseServiceImpl<UserGroup, Integer> implements UserGroupService {
    private final UserGroupRepository repository;
    protected UserGroupServiceImpl(UserGroupRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    public Optional<UserGroup> findByUserIdAndGroupId(Integer userId, Integer groupId) {
        return Optional.ofNullable(repository.findByUserIdAndGroupId(userId, groupId));
    }
}
