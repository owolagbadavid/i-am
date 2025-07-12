package me.oreos.iam.services.impl;

import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;

import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import lombok.extern.slf4j.Slf4j;
import me.oreos.iam.services.SecurityService;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SecurityServiceImpl implements SecurityService {

    private final RedisTemplate<String, String> redisTemplate;
    private final Environment environment;
    private final DefaultSecretGenerator secretGenerator;
    private final SystemTimeProvider timeProvider;
    private final DefaultCodeGenerator codeGenerator;
    private final DefaultCodeVerifier codeVerifier;

    public SecurityServiceImpl(RedisTemplate<String, String> redisTemplate, Environment environment) {
        this.redisTemplate = redisTemplate;
        this.environment = environment;
        this.secretGenerator = new DefaultSecretGenerator(32);
        this.timeProvider = new SystemTimeProvider();
        this.codeGenerator = new DefaultCodeGenerator(
                HashingAlgorithm.SHA1, 6);
        this.codeVerifier = new DefaultCodeVerifier(this.codeGenerator, timeProvider);
    }

    /**
     * Generate a TOTP secret for the user and store it in Redis.
     */
    @Override
    public String generateOtpSecret(String email, Duration expiry) {
        String secret;
        if ("production".equalsIgnoreCase(environment.getProperty("spring.profiles.active"))) {
            secret = secretGenerator.generate();
        } else {
            secret = "JBSWY3DPEHPK3PXP"; // static base32 key for dev (generates 123456)
        }

        String key = String.format("otp-secret:%s", email);
        redisTemplate.opsForValue().set(key, secret, expiry.toMillis(), TimeUnit.MILLISECONDS);

        return secret;
    }

    @Override
    public String generateTotp(String secret) throws Exception {

        return codeGenerator.generate(secret, timeProvider.getTime());

    }

    /**
     * Verify the OTP code sent by the user.
     */
    @Override
    public boolean verifyOtp(String email, String userInputCode) {
        String key = String.format("otp-secret:%s", email);
        String secret = redisTemplate.opsForValue().get(key);

        if (secret == null) {
            return false;
        }
        try {
            return codeVerifier.isValidCode(secret, userInputCode);
        } catch (Exception e) {
            log.error("Error verifying OTP for {}: {}", email, e.getMessage());
            return false;
        }
    }

    @Override
    public void deleteOtpSecret(String email) {
        String key = String.format("otp-secret:%s", email);
        redisTemplate.delete(key);
    }
}
