package me.oreos.iam.services;

import javax.servlet.http.HttpServletRequest;

import me.oreos.iam.Dtos.AuthorizationRequestDto;
import me.oreos.iam.Dtos.ForgotPasswordDto;
import me.oreos.iam.Dtos.LoginDto;
import me.oreos.iam.Dtos.OnboardAdminDto;
import me.oreos.iam.Dtos.ResetPasswordDto;

public interface AuthService {
    void authorize(AuthorizationRequestDto request);
    String loginHandler(LoginDto loginDto, HttpServletRequest request) throws Exception;
    public void resetPassword(ResetPasswordDto dto) throws Exception;
    public void forgotPassword(ForgotPasswordDto dto) throws Exception;
    void onboardAdmin(OnboardAdminDto dto) throws Exception;
}