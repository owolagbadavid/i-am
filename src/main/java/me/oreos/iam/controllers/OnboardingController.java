package me.oreos.iam.controllers;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wakanda.framework.response.dto.ResponseDTO;
import org.wakanda.framework.response.helper.ResponseHelper;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.oreos.iam.services.utils.Helper;
import me.oreos.iam.Dtos.*;
import me.oreos.iam.services.OnboardingService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Tag(name = "Onboarding", description = "Operations related to onboarding in the IAM system")
@RestController
@RequestMapping(value = "/api/v1/onboarding"
// , consumes = { "application/json", "application/org.wakanda.fw-v1+json" },
// produces = { "application/json", "application/org.wakanda.fw-v1+json" }
)
@RequiredArgsConstructor
public class OnboardingController {
    private final ResponseHelper<String> responseHelper;
    private final OnboardingService onboardingService;

    @PostMapping("/admin")
    public ResponseEntity<ResponseDTO<String>> onboardAdmin(@Valid @RequestBody OnboardAdminDto dto) {
        try {
            onboardingService.onboardAdmin(dto);
            return responseHelper.ok("Admin onboarded successfully", "");
        } catch (Exception e) {
            return Helper.errorHandler(e);
        }
    }

}