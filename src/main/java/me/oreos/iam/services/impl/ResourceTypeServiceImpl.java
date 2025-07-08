package me.oreos.iam.services.impl;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;
import org.wakanda.framework.service.BaseServiceImpl;

import lombok.extern.slf4j.Slf4j;
import me.oreos.iam.entities.ResourceType;
import me.oreos.iam.repositories.ResourceTypeRepository;
import me.oreos.iam.services.ResourceTypeService;

@Component
@Transactional
@Slf4j
public class ResourceTypeServiceImpl extends BaseServiceImpl<ResourceType, Integer> implements ResourceTypeService {
    protected ResourceTypeServiceImpl(ResourceTypeRepository repository) {
        super(repository);
    }
}
