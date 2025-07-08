package me.oreos.iam.services;


import org.springframework.stereotype.Service;
import org.wakanda.framework.service.BaseService;
import me.oreos.iam.entities.Policy;

@Service
public interface PolicyService extends BaseService<Policy, Integer> {
}
