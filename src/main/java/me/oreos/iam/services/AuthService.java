package me.oreos.iam.services;

import me.oreos.iam.Dtos.AuthorizationRequestDto;

public interface AuthService {

    void authorize(AuthorizationRequestDto request);

}