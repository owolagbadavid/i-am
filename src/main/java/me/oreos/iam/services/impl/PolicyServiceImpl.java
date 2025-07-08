package me.oreos.iam.services.impl;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;
import org.wakanda.framework.service.BaseServiceImpl;

import lombok.extern.slf4j.Slf4j;
import me.oreos.iam.entities.Policy;
import me.oreos.iam.repositories.PolicyRepository;
import me.oreos.iam.services.PolicyService;

@Component
@Transactional
@Slf4j
public class PolicyServiceImpl extends BaseServiceImpl<Policy, Integer> implements PolicyService {
    protected PolicyServiceImpl(PolicyRepository repository) {
        super(repository);
    }
}
