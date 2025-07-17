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
import me.oreos.iam.entities.User;
import me.oreos.iam.entities.UserGroup;
import me.oreos.iam.entities.enums.EffectiveScopeEnum;
import me.oreos.iam.entities.models.ResourcePermissionModel;
import me.oreos.iam.entities.models.UserPermissionModel;
import me.oreos.iam.repositories.ActionRepository;
import me.oreos.iam.repositories.PermissionRepository;
import me.oreos.iam.repositories.ResourceRepository;
import me.oreos.iam.repositories.UserGroupRepository;
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
    private final UserGroupRepository userGroupRepository;

    protected AuthServiceImpl(ActionRepository actionRepository, ResourceRepository resourceRepository,
            TokenService tokenService, TokenProvider tokenProvider, UserRepository userRepository,
            PermissionRepository permissionRepository, UserGroupRepository userGroupRepository) {
        this.actionRepository = actionRepository;
        this.resourceRepository = resourceRepository;
        this.tokenService = tokenService;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.userGroupRepository = userGroupRepository;
    }

    @Override
    public void authorize(AuthorizationRequestDto request) {
        var pair = getActionAndResource(request.action, request.resourceId);
        var action = pair.getLeft();
        var resource = pair.getRight();

        var claims = validateToken(request.authToken);

        var userPermissionPair = getUserPermissions(claims.getSubject(), action, resource);
        var userPermissions = userPermissionPair.getLeft();
        var user = userPermissionPair.getRight();
        var resourcePermissions = getResourcePermissions(resource.getResourceId());

        // Authorization logic here

        checkPermissions(userPermissions, user, action.getCode(), resource, resourcePermissions);
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

    private Pair<List<UserPermissionModel>, User> getUserPermissions(String email, Action action, Resource resource) {
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

        return Pair.of(permissions, user);
    }

    private List<ResourcePermissionModel> getResourcePermissions(Integer resourceId) {

        var permissionsMap = permissionRepository.findResourcePermissions(resourceId);

        Gson gson = new Gson();
        System.out.println("Resource Permissions: ");
        System.out.println(gson.toJson(permissionsMap));

        ObjectMapper mapper = new ObjectMapper();
        List<ResourcePermissionModel> permissions = mapper.convertValue(permissionsMap,
                new TypeReference<List<ResourcePermissionModel>>() {
                });

        return permissions;
    }

    private void checkPermissions(List<UserPermissionModel> userPermissions, User user, String actionCode,
            Resource resource,
            List<ResourcePermissionModel> resourcePermissions) throws BaseException {
        for (UserPermissionModel userPermission : userPermissions) {
            // Check if action and resource type match
            if (isActionAndResourceTypeValid(userPermission, actionCode, resource)) {
                // Check scope requirements
                if (checkPermissionScope(userPermission, user, resource)) {
                    return; // Access granted
                }
            }
        }

        throw new BaseException(403,
                "Access denied for action: " + actionCode + " on resource: " + resource.getResourceId());

    }

    private boolean isActionAndResourceTypeValid(UserPermissionModel userPermission, String actionCode,
            Resource resource) {
        return userPermission.getAction().equals(actionCode)
                && userPermission.getResourceTypeId().equals(resource.getResourceType().getId());
    }

    private boolean checkPermissionScope(UserPermissionModel userPermission, User user, Resource resource)
            throws BaseException {
        EffectiveScopeEnum scope = userPermission.getEffectiveScope();

        switch (scope) {
            case DEFAULT:
            case ALL:
                return true; // DEFAULT and ALL grant access without further checks
            case OWN:
                return checkOwnScope(user, resource);
            case ITEM:
                return checkItemScope(userPermission, resource);
            case GROUP:
                return checkGroupScope(user, resource);
            default:
                throw new BaseException(500, "Unsupported permission scope: " + scope);
        }
    }

    private boolean checkOwnScope(User user, Resource resource) {
        // Check if the resource belongs to the user
        return resource.getUser().getId() != null
                && resource.getUser().getId().equals(user.getId());
    }

    private boolean checkItemScope(UserPermissionModel userPermission, Resource resource) {
        // Check if the resourceId in the permission matches the resource's resourceId
        return userPermission.getResourceId() != null
                && userPermission.getResourceId().equals(resource.getResourceId());
    }

    private boolean checkGroupScope(User user, Resource resource) {
        // Check if the resource's group is one of the user's groups
        Integer groupId = resource.getGroup().getId();
        if (groupId == null) {
            return false;
        }

        UserGroup userGroup = userGroupRepository.findByUserIdAndGroupId(user.getId(), groupId);
        return userGroup != null;
    }
}