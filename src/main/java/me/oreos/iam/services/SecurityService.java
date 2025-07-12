package me.oreos.iam.services;

import java.time.Duration;

public interface SecurityService {

    /**
     * Generate a TOTP secret for the user and store it in Redis.
     */
    String generateOtpSecret(String email, Duration expiry);

    String generateTotp(String secret) throws Exception;

    /**
     * Verify the OTP code sent by the user.
     */
    boolean verifyOtp(String email, String userInputCode);

    void deleteOtpSecret(String email);

}