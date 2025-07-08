package me.oreos.iam.services;


import org.springframework.stereotype.Service;
import org.wakanda.framework.service.BaseService;
import me.oreos.iam.entities.ResourceType;

@Service
public interface ResourceTypeService extends BaseService<ResourceType, Integer> {
}
