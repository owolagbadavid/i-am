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

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.oreos.iam.Dtos.AssignPermissionRequest;
import me.oreos.iam.entities.Policy;
import me.oreos.iam.services.PolicyService;
import me.oreos.iam.services.utils.Helper;

@Tag(name = "Policy", description = "Operations related to policies in the IAM system")
@RestController
@RequestMapping(value = "/api/v1/policies"
// , consumes = { "application/json", "application/org.wakanda.fw-v1+json" },
// produces = { "application/json", "application/org.wakanda.fw-v1+json" }
)
public class PolicyController extends BaseQueryController<me.oreos.iam.entities.Policy, Integer> {
    private final PolicyService policyService;
    private final ResponseHelper<me.oreos.iam.entities.Policy> responseHelper;

    public PolicyController(PolicyService policyService, ResponseHelper<me.oreos.iam.entities.Policy> responseHelper) {
        super(policyService, responseHelper);
        this.policyService = policyService;
        this.responseHelper = responseHelper;
    }

    @PostMapping("{id}/permissions/{permissionId}")
    public ResponseEntity<ResponseDTO<Policy>> addPolicyPermission(@PathVariable Integer id,
            @PathVariable Integer permissionId, @RequestBody AssignPermissionRequest request) {
        try {
            var policy = policyService.addPolicyPermission(id, permissionId, request.effectiveScope);
            return responseHelper.success(
                    HttpStatus.CREATED,
                    ResponseType.CREATED,
                    "Policy permission added successfully",
                    policy);
        } catch (Exception e) {
            return Helper.errorHandler(e);
        }
    }
}
