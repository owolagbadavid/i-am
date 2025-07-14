package me.oreos.iam.repositories;

import org.springframework.stereotype.Repository;
import org.wakanda.framework.repository.BaseRepository;

import me.oreos.iam.entities.UserRole;

@Repository
public interface UserRoleRepository extends BaseRepository<me.oreos.iam.entities.UserRole, Integer> { 
    UserRole findByUserIdAndRoleId(Integer userId, Integer roleId);
}
