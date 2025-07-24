package me.oreos.iam.services.impl;

import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Component;
import org.wakanda.framework.exception.BaseException;

import lombok.extern.slf4j.Slf4j;
import me.oreos.iam.entities.Group;
import me.oreos.iam.entities.User;
import me.oreos.iam.repositories.GroupRepository;
import me.oreos.iam.repositories.UserRepository;
import me.oreos.iam.services.GroupService;
import me.oreos.iam.services.UserGroupService;

@Component
@Transactional
@Slf4j
public class GroupServiceImpl extends MyBaseServiceImpl<Group, Integer> implements GroupService {
    private final GroupRepository repository;
    private final UserGroupService userGroupService;
    private final UserRepository userRepository;

    protected GroupServiceImpl(GroupRepository repository, UserGroupService userGroupService,
            UserRepository userRepository) {
        super(repository);
        this.repository = repository;
        this.userRepository = userRepository;
        this.userGroupService = userGroupService;
    }

    @Override
    public Group addUserGroup(Integer groupId, Integer userId) {
        Optional<Group> groupOpt;
        Optional<User> userOpt;

        groupOpt = repository.findById(groupId);
        if (groupOpt.isEmpty()) {
            throw new BaseException(404, "Group not found");
        }

        userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new BaseException(404, "User not found");
        }

        var group = groupOpt.get();
        var user = userOpt.get();

        var alreadyExists = userGroupService.findByUserIdAndGroupId(userId, groupId);
        if (alreadyExists.isPresent()) {
            throw new BaseException(409, "User already exists in the group");
        }

        var userGroup = new me.oreos.iam.entities.UserGroup();
        userGroup.setUser(user);
        userGroup.setGroup(group);

        userGroupService.save(userGroup);

        return group;
    }

    @Override
    public Group removeUserGroup(Integer groupId, Integer userId) {
        Optional<Group> groupOpt = repository.findById(groupId);
        if (groupOpt.isEmpty()) {
            throw new BaseException(404, "Group not found");
        }

        var group = groupOpt.get();
        var userGroup = userGroupService.findByUserIdAndGroupId(userId, groupId);
        if (userGroup.isEmpty()) {
            throw new BaseException(404, "User is not a member of the group");
        }

        userGroupService.delete(userGroup.get().getId());
        return group;
    }
}
