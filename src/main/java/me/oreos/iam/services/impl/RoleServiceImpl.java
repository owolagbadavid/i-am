package me.oreos.iam.services.impl;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;
import org.wakanda.framework.service.BaseServiceImpl;

import lombok.extern.slf4j.Slf4j;
import me.oreos.iam.entities.Role;
import me.oreos.iam.repositories.RoleRepository;
import me.oreos.iam.services.RoleService;

@Component
@Transactional
@Slf4j
public class RoleServiceImpl extends BaseServiceImpl<Role, Integer> implements RoleService {
    protected RoleServiceImpl(RoleRepository repository) {
        super(repository);
    }
}
