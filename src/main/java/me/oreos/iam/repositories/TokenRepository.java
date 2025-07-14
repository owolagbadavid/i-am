package me.oreos.iam.repositories;

import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.wakanda.framework.repository.BaseRepository;

@Repository
public interface TokenRepository extends BaseRepository<me.oreos.iam.entities.Token, Integer> {
    Optional<me.oreos.iam.entities.Token> findByToken(String token);
}
