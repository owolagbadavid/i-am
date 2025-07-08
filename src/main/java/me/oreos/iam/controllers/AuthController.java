package me.oreos.iam.controllers;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.wakanda.framework.response.dto.ResponseDTO;
import org.wakanda.framework.response.enums.ResponseType;
import org.wakanda.framework.response.helper.ResponseHelper;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import me.oreos.iam.services.UserService;
import me.oreos.iam.services.utils.Helper;
import me.oreos.iam.Dtos.LoginDto;
import me.oreos.iam.entities.Token;
import me.oreos.iam.services.TokenProvider;
import me.oreos.iam.services.TokenService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Tag(name = "Auth", description = "Operations related to authentication in the IAM system")
@RestController
@Controller
@ResponseBody
@RequestMapping(value = "/api/v1/auth"
// , consumes = { "application/json", "application/org.wakanda.fw-v1+json" }, produces = { "application/json", "application/org.wakanda.fw-v1+json" }
)
public class AuthController  {

    private final TokenProvider tokenProvider;

    private final UserService userService;
    private final TokenService tokenService;
    private final ResponseHelper<String> responseHelper;
    public AuthController(UserService userService, TokenService tokenService, ResponseHelper<String> responseHelper, TokenProvider tokenProvider) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.responseHelper = responseHelper;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<String>> login(@RequestBody LoginDto loginDto, HttpServletRequest request) {
        var userOpt = userService.findByEmail(loginDto.emailAddress);

        if (userOpt.isEmpty()) {
            // throw new BaseException(401, "Inavlid credentials");
            return this.responseHelper.error(HttpStatus.UNAUTHORIZED, ResponseType.UNKNOWN_ERROR,  "Invalid credentials", "");
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
    }
}