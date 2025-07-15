package me.oreos.iam.services.impl;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.Instant;
import org.springframework.stereotype.Service;
import org.wakanda.framework.exception.BaseException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import io.jsonwebtoken.Claims;
import me.oreos.iam.Dtos.AuthorizationRequestDto;
import me.oreos.iam.entities.Action;
import me.oreos.iam.entities.Resource;
import me.oreos.iam.entities.ResourceType;
import me.oreos.iam.entities.models.UserPermissionModel;
import me.oreos.iam.repositories.ActionRepository;
import me.oreos.iam.repositories.PermissionRepository;
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
    private final PermissionRepository permissionRepository;

    protected AuthServiceImpl(ActionRepository actionRepository, ResourceRepository resourceRepository,
            TokenService tokenService, TokenProvider tokenProvider, UserRepository userRepository,
            PermissionRepository permissionRepository) {
        this.actionRepository = actionRepository;
        this.resourceRepository = resourceRepository;
        this.tokenService = tokenService;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public void authorize(AuthorizationRequestDto request) {
        var pair = getActionAndResource(request.action, request.resourceId);
        var action = pair.getLeft();
        var resource = pair.getRight();

        var claims = validateToken(request.authToken);

        var permissions = getUserPermissions(claims.getSubject(), action, resource);

        // Authorization logic here

        checkPermissions(permissions, action.getCode(), resource);
    }

    private Pair<Action, Resource> getActionAndResource(String actionCode, Integer resourceId) {
        var actionOpt = actionRepository.findByCode(actionCode);
        if (actionOpt.isEmpty()) {
            throw new BaseException(404, "Action not found: " + actionCode);
        }

        var resourceOpt = resourceRepository.findByResourceId(resourceId);
        if (resourceOpt.isEmpty()) {
            throw new BaseException(404, "Resource not found: " + resourceId);
        }

        var action = actionOpt.get();
        var resource = resourceOpt.get();

        return Pair.of(action, resource);
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

    private List<UserPermissionModel> getUserPermissions(String email, Action action, Resource resource) {
        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new BaseException(404, "User not found");
        }

        var user = userOpt.get();

        var permissionsMap = permissionRepository.findUserPermissions(user.getId(), action.getId(),
                resource.getResourceType().getId());

        Gson gson = new Gson();
        System.out.println(gson.toJson(permissionsMap));

        ObjectMapper mapper = new ObjectMapper();
        List<UserPermissionModel> permissions = mapper.convertValue(permissionsMap,
                new TypeReference<List<UserPermissionModel>>() {
                });

        return permissions;
    }

    private void checkPermissions(List<UserPermissionModel> permissions, String actionCode, Resource resource)
            throws BaseException {
        for (UserPermissionModel permission : permissions) {
            if (permission.getAction().equals(actionCode)
                    && permission.getResourceTypeId().equals(resource.getResourceType().getId())) {
                return; // Access granted
            }
        }

        throw new BaseException(403,
                "Access denied for action: " + actionCode + " on resource: " + resource.getResourceId());

    }
}