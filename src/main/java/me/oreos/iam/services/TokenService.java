package me.oreos.iam.services;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.wakanda.framework.service.BaseService;
import me.oreos.iam.entities.Token;

@Service
public interface TokenService extends BaseService<Token, Integer> {
    public Optional<Token> findByToken(String token);
}
