package me.oreos.iam.services.impl;

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
    protected TokenServiceImpl(TokenRepository repository) {
        super(repository);
    }
}
