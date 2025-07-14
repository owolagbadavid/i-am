package me.oreos.iam.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.wakanda.framework.controller.BaseQueryController;
import org.wakanda.framework.response.helper.ResponseHelper;

import io.swagger.v3.oas.annotations.tags.Tag;
import me.oreos.iam.services.ResourceService;

@Tag(name = "Resource", description = "Operations related to resources in the IAM system")
@RestController
@Controller
@ResponseBody
@RequestMapping(value = "/api/v1/resources"
// , consumes = { "application/json", "application/org.wakanda.fw-v1+json" }, produces = { "application/json", "application/org.wakanda.fw-v1+json" }
)
public class ResourceController extends BaseQueryController<me.oreos.iam.entities.Resource, Integer> {
    public ResourceController(ResourceService resourceService, ResponseHelper<me.oreos.iam.entities.Resource> responseHelper) {
        super(resourceService, responseHelper);
    }
}
