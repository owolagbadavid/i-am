package me.oreos.iam.controllers;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
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
import me.oreos.iam.entities.Token;
import me.oreos.iam.services.AuthService;
import me.oreos.iam.services.SecurityService;
import me.oreos.iam.services.TokenProvider;
import me.oreos.iam.services.TokenService;
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

    private final TokenProvider tokenProvider;
    private final UserService userService;
    private final TokenService tokenService;
    private final ResponseHelper<String> responseHelper;
    private final SecurityService securityService;
    private final AuthService authService;

    public AuthController(UserService userService, TokenService tokenService, ResponseHelper<String> responseHelper,
            TokenProvider tokenProvider, SecurityService securityService, AuthService authService) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.responseHelper = responseHelper;
        this.tokenProvider = tokenProvider;
        this.securityService = securityService;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<String>> login(@RequestBody LoginDto loginDto, HttpServletRequest request) {

        try {
            var userOpt = userService.findByEmail(loginDto.emailAddress);

            if (userOpt.isEmpty()) {
                // throw new BaseException(401, "Inavlid credentials");
                return this.responseHelper.error(HttpStatus.UNAUTHORIZED, ResponseType.UNKNOWN_ERROR,
                        "Invalid credentials", "");
            }

            String ipAddress = Helper.getClientIp(request);
            String deviceInfo = request.getHeader("User-Agent");
            String loginLocation = null;
            try {
                loginLocation = Helper.getGeoLocation(ipAddress).toString();
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            var user = userOpt.get();

            if (!PasswordHasher.verifyPassword(loginDto.password, user.getPasswordHash())) {
                return this.responseHelper.error(HttpStatus.UNAUTHORIZED, ResponseType.UNKNOWN_ERROR,
                        "Invalid credentials", "");
            }

            var tokenString = tokenProvider.generateToken(user.getEmail());
            var claims = tokenProvider.validateToken(tokenString);

            var token = new Token();
            token.setUser(user);
            token.setToken(tokenString);
            token.setExpiresAt(new DateTime(claims.getExpiration())); // 1 hour expiration
            token.setIpAddress(ipAddress);
            token.setDeviceInfo(deviceInfo);
            token.setLoginLocation(loginLocation);

            token = tokenService.save(token);
            return responseHelper.ok("login successful", token.getToken());
        } catch (Exception e) {
            // log.error("Login failed: {}", e.getMessage());
            return responseHelper.error(HttpStatus.UNAUTHORIZED, ResponseType.UNKNOWN_ERROR, "Invalid credentials", "");
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
            // log.error("Forgot password failed: {}", e.getMessage());
            return responseHelper.error(HttpStatus.INTERNAL_SERVER_ERROR, ResponseType.UNKNOWN_ERROR,
                    "Unexpected error", "");
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
            // log.error("Reset password failed: {}", e.getMessage());
            return responseHelper.error(HttpStatus.INTERNAL_SERVER_ERROR, ResponseType.UNKNOWN_ERROR,
                    "Unexpected error", "");
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