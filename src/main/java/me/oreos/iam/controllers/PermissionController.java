package me.oreos.iam.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wakanda.framework.controller.BaseQueryController;
import org.wakanda.framework.response.helper.ResponseHelper;

import io.swagger.v3.oas.annotations.tags.Tag;
import me.oreos.iam.services.PermissionService;

@Tag(name = "Permission", description = "Operations related to permissions in the IAM system")
@RestController
@RequestMapping(value = "/api/v1/permissions"
// , consumes = { "application/json", "application/org.wakanda.fw-v1+json" }, produces = { "application/json", "application/org.wakanda.fw-v1+json" }
)
public class PermissionController extends BaseQueryController<me.oreos.iam.entities.Permission, Integer> {
    public PermissionController(PermissionService permissionService, ResponseHelper<me.oreos.iam.entities.Permission> responseHelper) {
        super(permissionService, responseHelper);
    }
}
