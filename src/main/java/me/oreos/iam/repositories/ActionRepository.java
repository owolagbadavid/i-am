package me.oreos.iam.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.wakanda.framework.repository.BaseRepository;

import me.oreos.iam.entities.Action;

@Repository
public interface ActionRepository extends BaseRepository<me.oreos.iam.entities.Action, Integer> { 
    Action findByCode(String code);
    
    Action findDistinctByCode(String code);

    List<Action> findAllByIsActiveTrue();
}
