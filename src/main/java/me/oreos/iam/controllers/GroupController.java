package me.oreos.iam.controllers;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.wakanda.framework.controller.BaseQueryController;
import org.wakanda.framework.response.dto.ResponseDTO;
import org.wakanda.framework.response.enums.ResponseType;
import org.wakanda.framework.response.helper.ResponseHelper;

import io.swagger.v3.oas.annotations.tags.Tag;
import me.oreos.iam.entities.Group;
import me.oreos.iam.entities.User;
import me.oreos.iam.services.GroupService;
import me.oreos.iam.services.UserGroupService;
import me.oreos.iam.services.UserService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Group", description = "Operations related to groups in the IAM system")
@RestController
@Controller
@ResponseBody
@RequestMapping(value = "/api/v1/groups"
// , consumes = { "application/json", "application/org.wakanda.fw-v1+json" },
// produces = { "application/json", "application/org.wakanda.fw-v1+json" }
)
public class GroupController extends BaseQueryController<me.oreos.iam.entities.Group, Integer> {
    private final GroupService groupService;
    private final ResponseHelper<me.oreos.iam.entities.Group> responseHelper;
    private final UserGroupService userGroupService;
    private final UserService userService;

    public GroupController(GroupService groupService, ResponseHelper<me.oreos.iam.entities.Group> responseHelper,
            UserGroupService userGroupService, UserService userService) {
        super(groupService, responseHelper);
        this.groupService = groupService;
        this.responseHelper = responseHelper;
        this.userGroupService = userGroupService;
        this.userService = userService;
    }

    // add user to group
    @PostMapping("{groupId}/users/{userId}")
    public ResponseEntity<ResponseDTO<Group>> postMethodName(@RequestBody String entity,
            @PathVariable Integer groupId,
            @PathVariable Integer userId) {

        Optional<Group> groupOpt;
        Optional<User> userOpt;

        try {
            groupOpt = groupService.findById(groupId);
            if (groupOpt.isEmpty()) {
                return responseHelper.error(HttpStatus.NOT_FOUND, ResponseType.NOT_FOUND, "Group not found");
            }
        } catch (Exception e) {
            return responseHelper.error(HttpStatus.NOT_FOUND, ResponseType.NOT_FOUND, "Group not found");
        }

        try {
            userOpt = userService.findById(userId);
            if (userOpt.isEmpty()) {
                return responseHelper.error(HttpStatus.NOT_FOUND, ResponseType.NOT_FOUND, "User not found");
            }
        } catch (Exception e) {
            return responseHelper.error(HttpStatus.NOT_FOUND, ResponseType.NOT_FOUND, "User not found");
        }

        var group = groupOpt.get();
        var user = userOpt.get();

        var alreadyExists = userGroupService.findByUserIdAndGroupId(userId, groupId);
        if (alreadyExists.isPresent()) {
            return responseHelper.error(HttpStatus.CONFLICT, ResponseType.CONFLICT, "User already exists in the group");
        }

        var userGroup = new me.oreos.iam.entities.UserGroup();
        userGroup.setUser(user);
        userGroup.setGroup(group);

        userGroupService.save(userGroup);

        return responseHelper.success(
                HttpStatus.CREATED,
                ResponseType.CREATED,
                "User added to group successfully",
                group);
    }

    @DeleteMapping("{groupId}/users/{userId}")
    public ResponseEntity<ResponseDTO<Group>> removeUserFromGroup(
            @PathVariable Integer groupId,
            @PathVariable Integer userId) {

        Optional<Group> groupOpt;
        Optional<User> userOpt;

        try {
            groupOpt = groupService.findById(groupId);
            if (groupOpt.isEmpty()) {
                return responseHelper.error(HttpStatus.NOT_FOUND, ResponseType.NOT_FOUND, "Group not found");
            }
        } catch (Exception e) {
            return responseHelper.error(HttpStatus.NOT_FOUND, ResponseType.NOT_FOUND, "Group not found");
        }

        var group = groupOpt.get();

        var userGroup = userGroupService.findByUserIdAndGroupId(userId, groupId);
        if (userGroup.isEmpty()) {
            return responseHelper.error(HttpStatus.NOT_FOUND, ResponseType.NOT_FOUND,
                    "User is not a member of the group");
        }

        userGroupService.delete(userGroup.get().getId());

        return responseHelper.success(
                HttpStatus.OK,
                ResponseType.OK,
                "User removed from group successfully",
                group);
    }
}
