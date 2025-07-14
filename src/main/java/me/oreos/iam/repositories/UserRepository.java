package me.oreos.iam.repositories;


import org.springframework.stereotype.Repository;
import org.wakanda.framework.repository.BaseRepository;

import me.oreos.iam.entities.User;

@Repository
public interface UserRepository extends BaseRepository<me.oreos.iam.entities.User, Integer> { 
    User findByEmail(String email);
    
    User findDistinctByEmail(String email);

}
