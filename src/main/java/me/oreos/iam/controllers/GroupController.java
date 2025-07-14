package me.oreos.iam.controllers;

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
import me.oreos.iam.services.GroupService;
import me.oreos.iam.services.utils.Helper;

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

    public GroupController(GroupService groupService, ResponseHelper<me.oreos.iam.entities.Group> responseHelper) {
        super(groupService, responseHelper);
        this.groupService = groupService;
        this.responseHelper = responseHelper;
    }

    // add user to group
    @PostMapping("{groupId}/users/{userId}")
    public ResponseEntity<ResponseDTO<Group>> postMethodName(@RequestBody String entity,
            @PathVariable Integer groupId,
            @PathVariable Integer userId) {

        try {
            var group = groupService.addUserGroup(groupId, userId);

            return responseHelper.success(
                    HttpStatus.CREATED,
                    ResponseType.CREATED,
                    "User added to group successfully",
                    group);
        } catch (Exception e) {
            return Helper.errorHandler(e);
        }

    }

    @DeleteMapping("{groupId}/users/{userId}")
    public ResponseEntity<ResponseDTO<Group>> removeUserFromGroup(
            @PathVariable Integer groupId,
            @PathVariable Integer userId) {

        try {
            var group = groupService.removeUserGroup(groupId, userId);

            return responseHelper.success(
                    HttpStatus.OK,
                    ResponseType.OK,
                    "User removed from group successfully",
                    group);
        } catch (Exception e) {
            return Helper.errorHandler(e);
        }
    }
}
