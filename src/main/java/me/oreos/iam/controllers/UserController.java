package me.oreos.iam.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wakanda.framework.controller.BaseQueryController;
import org.wakanda.framework.response.dto.ResponseDTO;
import org.wakanda.framework.response.enums.ResponseType;
import org.wakanda.framework.response.helper.ResponseHelper;

import io.swagger.v3.oas.annotations.tags.Tag;
import me.oreos.iam.annotation.CustomAuthorize;
import me.oreos.iam.entities.User;
import me.oreos.iam.services.UserService;
import me.oreos.iam.services.utils.Helper;

@Tag(name = "User", description = "Operations related to users in the IAM system")
@RestController
@RequestMapping(value = "/api/v1/users"
// , consumes = { "application/json", "application/org.wakanda.fw-v1+json" }
// ,produces = { "application/json", "application/org.wakanda.fw-v1+json" }
)
@CustomAuthorize(action = "read", resourceType = "user")
public class UserController extends BaseQueryController<me.oreos.iam.entities.User, Integer> {
    private final UserService userService;
    private final ResponseHelper<me.oreos.iam.entities.User> responseHelper;

    public UserController(UserService userService, ResponseHelper<me.oreos.iam.entities.User> responseHelper) {
        super(userService, responseHelper);
        this.userService = userService;
        this.responseHelper = responseHelper;
    }

    // assign role to user
    @PostMapping("{id}/roles/{roleId}")
    public ResponseEntity<ResponseDTO<User>> addUserRole(@PathVariable Integer id, @PathVariable Integer roleId) {
        try {
            var user = userService.addUserRole(id, roleId);
            return responseHelper.success(
                    HttpStatus.CREATED,
                    ResponseType.CREATED,
                    "User role added successfully",
                    user);
        } catch (Exception e) {
            return Helper.errorHandler(e);
        }
    }

    @PostMapping("{id}/policies/{policyId}")
    public ResponseEntity<ResponseDTO<User>> addUserPolicy(@PathVariable Integer id, @PathVariable Integer policyId) {
        try {
            var user = userService.addUserPolicy(id, policyId);
            return responseHelper.success(
                    HttpStatus.CREATED,
                    ResponseType.CREATED,
                    "User policy added successfully",
                    user);
        } catch (Exception e) {
            return Helper.errorHandler(e);
        }
    }
}