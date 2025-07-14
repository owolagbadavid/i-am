package me.oreos.iam.services.impl;

import org.springframework.stereotype.Service;

import me.oreos.iam.Dtos.AuthorizationRequestDto;
import me.oreos.iam.services.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    protected AuthServiceImpl() {

    }

    @Override
    public void authorize(AuthorizationRequestDto request) {
        // Authorization logic here
    }
}
