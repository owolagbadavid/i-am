package me.oreos.iam.services.impl;

import org.joda.time.Instant;
import org.springframework.stereotype.Service;
import org.wakanda.framework.exception.BaseException;

import io.jsonwebtoken.Claims;
import me.oreos.iam.Dtos.AuthorizationRequestDto;
import me.oreos.iam.repositories.ActionRepository;
import me.oreos.iam.repositories.ResourceRepository;
import me.oreos.iam.repositories.UserRepository;
import me.oreos.iam.services.AuthService;
import me.oreos.iam.services.TokenProvider;
import me.oreos.iam.services.TokenService;

@Service
public class AuthServiceImpl implements AuthService {
    private final ActionRepository actionRepository;
    private final ResourceRepository resourceRepository;
    private final TokenService tokenService;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    protected AuthServiceImpl(ActionRepository actionRepository, ResourceRepository resourceRepository,
            TokenService tokenService, TokenProvider tokenProvider, UserRepository userRepository) {
        this.actionRepository = actionRepository;
        this.resourceRepository = resourceRepository;
        this.tokenService = tokenService;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    public void authorize(AuthorizationRequestDto request) {
        var actionOpt = actionRepository.findByCode(request.action);

        if (actionOpt.isEmpty()) {
            throw new BaseException(404, "Action not found: " + request.action);
        }

        var resourceOpt = resourceRepository.findByResourceId(request.resourceId);
        if (resourceOpt.isEmpty()) {
            throw new BaseException(404, "Resource not found: " + request.resourceId);
        }

        var action = actionOpt.get();
        var resource = resourceOpt.get();

        var claims = validateToken(request.authToken);

        var userOpt = userRepository.findByEmail(claims.getSubject());
        if (userOpt.isEmpty()) {
            throw new BaseException(404, "User not found");
        }

        var user = userOpt.get();

        System.out.println("User: " + user.getEmail() + ", Action: " + action.getCode() + ", Resource: "
                + resource.getResourceId());

        // Authorization logic here
    }

    private Claims validateToken(String token) {
        var tokenOpt = tokenService.findByToken(token);
        if (tokenOpt.isEmpty()) {
            throw new BaseException(401, "Invalid token");
        }

        var claims = tokenProvider.validateToken(token);
        var isExpired = tokenProvider.isTokenExpired(claims);

        if (isExpired) {
            throw new BaseException(401, "Invalid token");
        }

        if (tokenOpt.get().getExpiresAt().isBefore(new Instant().getMillis())) {
            throw new BaseException(401, "Invalid token");
        }
        return claims;
    }
}
