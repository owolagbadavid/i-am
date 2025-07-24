package me.oreos.iam.controllers;

import javax.servlet.http.HttpServletRequest;
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
import me.oreos.iam.services.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Tag(name = "Auth", description = "Operations related to authentication in the IAM system")
@RestController
@RequestMapping(value = "/api/v1/auth"
// , consumes = { "application/json", "application/org.wakanda.fw-v1+json" },
// produces = { "application/json", "application/org.wakanda.fw-v1+json" }
)
@RequiredArgsConstructor
public class AuthController {
    private final ResponseHelper<String> responseHelper;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<String>> login(@Valid @RequestBody LoginDto loginDto, HttpServletRequest request) {

        try {
            var token = authService.loginHandler(loginDto, request);
            return responseHelper.ok("login successful", token);
        } catch (Exception e) {
            return Helper.errorHandler(e);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseDTO<String>> forgotPassword(@Valid @RequestBody ForgotPasswordDto dto) {
        try {
            authService.forgotPassword(dto);
            return responseHelper.ok("Password reset email sent", "");
        } catch (Exception e) {
            return Helper.errorHandler(e);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseDTO<String>> resetPassword(@Valid @RequestBody ResetPasswordDto dto) {
        try {
            authService.resetPassword(dto);
            return responseHelper.ok("Password reset successfully", "");
        } catch (Exception e) {
            return Helper.errorHandler(e);
        }
    }

    @PostMapping("/authorization")
    public ResponseEntity<ResponseDTO<String>> authorizeUser(@RequestBody AuthorizationRequestDto dto) {
        try {
            authService.authorize(dto);
            return responseHelper.ok("Authorization successful", "");
        } catch (Exception e) {
            return Helper.errorHandler(e);
        }
    }

}