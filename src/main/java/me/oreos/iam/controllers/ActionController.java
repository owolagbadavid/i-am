package me.oreos.iam.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wakanda.framework.controller.BaseQueryController;
import org.wakanda.framework.response.helper.ResponseHelper;

import io.swagger.v3.oas.annotations.tags.Tag;
import me.oreos.iam.services.ActionService;

@Tag(name = "Action", description = "Operations related to actions in the IAM system")
@RestController
@RequestMapping(value = "/api/v1/actions"
// , consumes = { "application/json", "application/org.wakanda.fw-v1+json" }, produces = { "application/json", "application/org.wakanda.fw-v1+json" }
)
public class ActionController extends BaseQueryController<me.oreos.iam.entities.Action, Integer> {
    public ActionController(ActionService actionService, ResponseHelper<me.oreos.iam.entities.Action> responseHelper) {
        super(actionService, responseHelper);
    }
}
