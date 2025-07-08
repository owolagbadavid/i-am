package me.oreos.iam.repositories;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.wakanda.framework.repository.BaseRepository;

import me.oreos.iam.entities.User;

@Repository
public interface UserRepository extends BaseRepository<me.oreos.iam.entities.User, Integer> { 
    Optional<User> findByEmail(String email);
    
    Optional<User> findDistinctByEmail(String email);

}
