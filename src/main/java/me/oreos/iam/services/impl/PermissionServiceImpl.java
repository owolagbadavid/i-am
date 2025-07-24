package me.oreos.iam.services.impl;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Component;
import org.wakanda.framework.service.BaseServiceImpl;

import lombok.extern.slf4j.Slf4j;
import me.oreos.iam.entities.Permission;
import me.oreos.iam.repositories.PermissionRepository;
import me.oreos.iam.services.PermissionService;

@Component
@Transactional
@Slf4j
public class PermissionServiceImpl extends BaseServiceImpl<Permission, Integer> implements PermissionService {
    protected PermissionServiceImpl(PermissionRepository repository) {
        super(repository);
    }
}
