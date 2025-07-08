package me.oreos.iam.services;


import org.springframework.stereotype.Service;
import org.wakanda.framework.service.BaseService;
import me.oreos.iam.entities.Resource;

@Service
public interface ResourceService extends BaseService<Resource, Integer> {
}
