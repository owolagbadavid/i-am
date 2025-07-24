package me.oreos.iam.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Component;
import org.wakanda.framework.service.BaseServiceImpl;

import lombok.extern.slf4j.Slf4j;
import me.oreos.iam.entities.Action;
import me.oreos.iam.repositories.ActionRepository;
import me.oreos.iam.services.ActionService;

@Component
@Transactional
@Slf4j
public class ActionServiceImpl extends BaseServiceImpl<Action, Integer> implements ActionService {
    private final ActionRepository actionRepository;

    protected ActionServiceImpl(ActionRepository actionRepository) {
        super(actionRepository);
        this.actionRepository = actionRepository;
    }

    @Override
    public Optional<Action> findByCode(String code) {
        // log.debug("Finding action by code: {}", code);
        return actionRepository.findByCode(code);
    }

    @Override
    public Optional<Action> findDistinctByCode(String code) {
        // log.debug("Finding distinct action by code: {}", code);
        return actionRepository.findDistinctByCode(code);
    }

    @Override
    public List<Action> findAllByIsActiveTrue() {
        // log.debug("Retrieving all active actions");
        return actionRepository.findAllByIsActiveTrue();
    }
}
