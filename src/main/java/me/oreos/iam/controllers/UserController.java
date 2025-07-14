package me.oreos.iam.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.wakanda.framework.controller.BaseQueryController;
import org.wakanda.framework.response.dto.ResponseDTO;
import org.wakanda.framework.response.enums.ResponseType;
import org.wakanda.framework.response.helper.ResponseHelper;

import io.swagger.v3.oas.annotations.tags.Tag;
import me.oreos.iam.entities.User;
import me.oreos.iam.services.UserService;
import me.oreos.iam.services.utils.Helper;

@Tag(name = "User", description = "Operations related to users in the IAM system")
@RestController
@Controller
@ResponseBody
@RequestMapping(value = "/api/v1/users"
// , consumes = { "application/json", "application/org.wakanda.fw-v1+json" }
// ,produces = { "application/json", "application/org.wakanda.fw-v1+json" }
)
public class UserController extends BaseQueryController<me.oreos.iam.entities.User, Integer> {
    private final UserService userService;
    private final ResponseHelper<me.oreos.iam.entities.User> responseHelper;

    public UserController(UserService userService, ResponseHelper<me.oreos.iam.entities.User> responseHelper) {
        super(userService, responseHelper);
        this.userService = userService;
        this.responseHelper = responseHelper;
    }

    // assign role to user
    @PostMapping("{userId}/roles/{roleId}")
    public ResponseEntity<ResponseDTO<User>> addUserRole(@PathVariable Integer userId, @PathVariable Integer roleId) {
        try {
            var user = userService.addUserRole(userId, roleId);
            return responseHelper.success(
                    HttpStatus.CREATED,
                    ResponseType.CREATED,
                    "User role added successfully",
                    user);
        } catch (Exception e) {
            return Helper.errorHandler(e);
        }
    }
}