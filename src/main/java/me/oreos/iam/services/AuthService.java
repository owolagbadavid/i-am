package me.oreos.iam.services;

import javax.servlet.http.HttpServletRequest;

import me.oreos.iam.Dtos.AuthorizationRequestDto;
import me.oreos.iam.Dtos.LoginDto;

public interface AuthService {

    void authorize(AuthorizationRequestDto request);

    String loginHandler(LoginDto loginDto, HttpServletRequest request) throws Exception;
}