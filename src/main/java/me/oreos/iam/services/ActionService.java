package me.oreos.iam.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.wakanda.framework.service.BaseService;

import me.oreos.iam.entities.Action;

@Service
public interface ActionService extends BaseService<Action, Integer> {
    /**
     * Finds an action by its code.
     *
     * @param code the code of the action
     * @return the action with the specified code, or null if not found
     */
    Optional<Action> findByCode(String code);
    
    /**
     * Finds an action by its code, ensuring that only one result is returned.
     *
     * @param code the code of the action
     * @return the action with the specified code, or null if not found
     */
    Optional<Action> findDistinctByCode(String code);

    /**
     * Retrieves all active actions.
     *
     * @return a list of all active actions
     */
    List<Action> findAllByIsActiveTrue();

}
