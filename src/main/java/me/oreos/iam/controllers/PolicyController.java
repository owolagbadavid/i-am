package me.oreos.iam.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.wakanda.framework.controller.BaseQueryController;
import org.wakanda.framework.response.helper.ResponseHelper;

import io.swagger.v3.oas.annotations.tags.Tag;
import me.oreos.iam.services.PolicyService;

@Tag(name = "Policy", description = "Operations related to policies in the IAM system")
@RestController
@Controller
@ResponseBody
@RequestMapping(value = "/api/v1/policies"
// , consumes = { "application/json", "application/org.wakanda.fw-v1+json" }, produces = { "application/json", "application/org.wakanda.fw-v1+json" }
)
public class PolicyController extends BaseQueryController<me.oreos.iam.entities.Policy, Integer> {
    public PolicyController(PolicyService policyService, ResponseHelper<me.oreos.iam.entities.Policy> responseHelper) {
        super(policyService, responseHelper);
    }
}
