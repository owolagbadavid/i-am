package me.oreos.iam.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wakanda.framework.response.dto.ResponseDTO;
import org.wakanda.framework.response.enums.ResponseType;
import org.wakanda.framework.response.helper.ResponseHelper;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import me.oreos.iam.services.UserService;
import me.oreos.iam.services.utils.Helper;
import me.oreos.iam.services.utils.PasswordHasher;
import me.oreos.iam.Dtos.*;
import me.oreos.iam.services.AuthService;
import me.oreos.iam.services.SecurityService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Tag(name = "Auth", description = "Operations related to authentication in the IAM system")
@RestController
@RequestMapping(value = "/api/v1/auth"
// , consumes = { "application/json", "application/org.wakanda.fw-v1+json" },
// produces = { "application/json", "application/org.wakanda.fw-v1+json" }
)
public class AuthController {

    private final UserService userService;
    private final ResponseHelper<String> responseHelper;
    private final SecurityService securityService;
    private final AuthService authService;

    public AuthController(UserService userService, ResponseHelper<String> responseHelper,
            SecurityService securityService, AuthService authService) {
        this.userService = userService;
        this.responseHelper = responseHelper;
        this.securityService = securityService;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<String>> login(@RequestBody LoginDto loginDto, HttpServletRequest request) {

        try {
            var token = authService.loginHandler(loginDto, request);
            return responseHelper.ok("login successful", token);
        } catch (Exception e) {
            return Helper.errorHandler(e);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseDTO<String>> forgotPassword(@RequestBody ForgotPasswordDto dto) {
        try {
            var userOpt = userService.findByEmail(dto.emailAddress);

            // Always return success to avoid user enumeration
            if (userOpt.isEmpty()) {
                return responseHelper.ok("Password reset email sent", "");
            }

            var user = userOpt.get();
            var otp = securityService.generateOtp(user.getEmail());

            System.out.println("Generated OTP: " + otp); // For debugging, remove in production

            // mailService.sendResetPasswordEmail(new ResetPasswordEmailRequest(
            // user.getProfile().getFullName(),
            // user.getEmailAddress(),
            // otp
            // ));

            return responseHelper.ok("Password reset email sent", "");
        } catch (Exception e) {
            return Helper.errorHandler(e);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseDTO<String>> resetPassword(@RequestBody ResetPasswordDto dto) {
        try {

            if (!securityService.verifyOtp(dto.emailAddress, dto.otp)) {
                return responseHelper.error(HttpStatus.UNAUTHORIZED, ResponseType.UNKNOWN_ERROR, "Invalid reset OTP",
                        "");
            }

            var userOpt = userService.findByEmail(dto.emailAddress);

            if (userOpt.isEmpty()) {
                return responseHelper.error(HttpStatus.UNAUTHORIZED, ResponseType.UNKNOWN_ERROR, "User not found", "");
            }

            var user = userOpt.get();
            user.setPasswordHash(PasswordHasher.hashPassword(dto.newPassword));

            userService.save(user);

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