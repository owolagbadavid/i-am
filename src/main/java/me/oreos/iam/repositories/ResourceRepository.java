package me.oreos.iam.repositories;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.wakanda.framework.repository.BaseRepository;

@Repository
public interface ResourceRepository extends BaseRepository<me.oreos.iam.entities.Resource, Integer> { 
    Optional<me.oreos.iam.entities.Resource> findByResourceId(Integer resourceId);
    Optional<me.oreos.iam.entities.Resource> findByResourceIdAndResourceTypeId(Integer resourceId, Integer resourceTypeId);
}
