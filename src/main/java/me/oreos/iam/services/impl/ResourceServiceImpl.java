package me.oreos.iam.services.impl;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Component;
import org.wakanda.framework.service.BaseServiceImpl;

import lombok.extern.slf4j.Slf4j;
import me.oreos.iam.entities.Resource;
import me.oreos.iam.repositories.ResourceRepository;
import me.oreos.iam.services.ResourceService;

@Component
@Transactional
@Slf4j
public class ResourceServiceImpl extends BaseServiceImpl<Resource, Integer> implements ResourceService {
    protected ResourceServiceImpl(ResourceRepository repository) {
        super(repository);
    }
}
