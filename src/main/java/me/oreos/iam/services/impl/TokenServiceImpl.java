package me.oreos.iam.services.impl;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Component;
import org.wakanda.framework.service.BaseServiceImpl;

import lombok.extern.slf4j.Slf4j;
import me.oreos.iam.entities.Token;
import me.oreos.iam.repositories.TokenRepository;
import me.oreos.iam.services.TokenService;

@Component
@Transactional
@Slf4j
public class TokenServiceImpl extends BaseServiceImpl<Token, Integer> implements TokenService {
    private final TokenRepository tokenRepository;

    protected TokenServiceImpl(TokenRepository tokenRepository) {
        super(tokenRepository);
        this.tokenRepository = tokenRepository;
    }

    @Override
    public Optional<Token> findByToken(String token) {
        return tokenRepository.findByToken(token);
    }
}
