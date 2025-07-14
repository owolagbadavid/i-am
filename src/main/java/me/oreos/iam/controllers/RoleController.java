package me.oreos.iam.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.wakanda.framework.controller.BaseQueryController;
import org.wakanda.framework.response.dto.ResponseDTO;
import org.wakanda.framework.response.enums.ResponseType;
import org.wakanda.framework.response.helper.ResponseHelper;

import io.swagger.v3.oas.annotations.tags.Tag;
import me.oreos.iam.Dtos.AssignPermissionRequest;
import me.oreos.iam.entities.Role;
import me.oreos.iam.services.RoleService;
import me.oreos.iam.services.utils.Helper;

@Tag(name = "Role", description = "Operations related to roles in the IAM system")
@RestController
@Controller
@ResponseBody
@RequestMapping(value = "/api/v1/roles"
// , consumes = { "application/json", "application/org.wakanda.fw-v1+json" },
// produces = { "application/json", "application/org.wakanda.fw-v1+json" }
)
public class RoleController extends BaseQueryController<me.oreos.iam.entities.Role, Integer> {
    private final RoleService roleService;
    private final ResponseHelper<me.oreos.iam.entities.Role> responseHelper;

    public RoleController(RoleService roleService, ResponseHelper<me.oreos.iam.entities.Role> responseHelper) {
        super(roleService, responseHelper);
        this.roleService = roleService;
        this.responseHelper = responseHelper;
    }

    @PostMapping("{id}/permissions/{permissionId}")
    public ResponseEntity<ResponseDTO<Role>> addRolePermission(@PathVariable Integer id,
            @PathVariable Integer permissionId, @RequestBody AssignPermissionRequest request) {
        try {
            var role = roleService.addRolePermission(id, permissionId, request.effectiveScope);
            return responseHelper.success(
                    HttpStatus.CREATED,
                    ResponseType.CREATED,
                    "Role permission added successfully",
                    role);
        } catch (Exception e) {
            return Helper.errorHandler(e);
        }
    }
}
