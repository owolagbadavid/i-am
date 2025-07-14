package me.oreos.iam.services;

import java.time.Duration;

public interface SecurityService {

    boolean verifyOtp(String email, String code);
    String generateOtp(String email);
    void deleteOtp(String email);

    // TOTP methods
    
    String generateTotpSecret(String email, Duration expiry);
    String generateTotp(String secret) throws Exception;
    boolean verifyTotp(String secret, String code);
    String[] generateRecoveryCode();
}