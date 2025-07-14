package me.oreos.iam.services.impl;

import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.recovery.RecoveryCodeGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import lombok.extern.slf4j.Slf4j;
import me.oreos.iam.services.SecurityService;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private final RecoveryCodeGenerator recoveryCodeGenerator;

    public SecurityServiceImpl(RedisTemplate<String, String> redisTemplate, Environment environment) {
        this.redisTemplate = redisTemplate;
        this.environment = environment;
        this.secretGenerator = new DefaultSecretGenerator();
        this.timeProvider = new SystemTimeProvider();
        this.codeGenerator = new DefaultCodeGenerator();
        this.codeVerifier = new DefaultCodeVerifier(this.codeGenerator, timeProvider);
        this.recoveryCodeGenerator = new RecoveryCodeGenerator();
        this.codeVerifier.setTimePeriod(10000000);
    }

    public String generateOtp(String email) {
        String key = String.format("otp:%s", email);

        String otp;

        // if not in production, use 123456 as OTP for testing
        if (!environment.acceptsProfiles(Profiles.of("prod"))) {
            otp = "123456";
        } else {
            // generate random 6-digit OTP
            otp = String.format("%06d", (int) (Math.random() * 1000000));
        }
        redisTemplate.opsForValue().set(key, otp, 10, TimeUnit.MINUTES);
        return otp;
    }

    /**
     * Verify the OTP code sent by the user.
     */
    @Override
    public boolean verifyOtp(String email, String code) {
        String key = String.format("otp:%s", email);
        String otp = redisTemplate.opsForValue().get(key);
        if (otp == null) {
            return false;
        }
        return otp.equals(code);
    }

    /**
     * Delete the OTP code for the user.
     */
    @Override
    public void deleteOtp(String email) {
        String key = String.format("otp:%s", email);
        redisTemplate.delete(key);
    }

    /// TOTP (Time-based One-Time Password) methods

    /**
     * Generate a TOTP secret for the user and store it in Redis.
     */
    @Override
    public String generateTotpSecret(String email, Duration expiry) {
        String secret;
        secret = secretGenerator.generate();

        // try {
        // QrData data = new QrData.Builder()
        // .label("example@example.com")
        // .secret(secret)
        // .issuer("AppName")
        // .algorithm(HashingAlgorithm.SHA1) // More on this below
        // .digits(6)
        // .period(60)
        // .build();

        // QrGenerator generator = new ZxingPngQrGenerator();
        // byte[] imageData = generator.generate(data);

        // File outputFile = new File("output/qr-code.png");

        // try {
        // // Ensure parent directories exist
        // outputFile.getParentFile().mkdirs();

        // // Write the bytes to the file
        // FileOutputStream fos = new FileOutputStream(outputFile);
        // fos.write(imageData);
        // fos.close();

        // System.out.println("QR code image saved at: " +
        // outputFile.getAbsolutePath());
        // } catch (IOException e) {
        // e.printStackTrace();
        // }

        // } catch (Exception e) {
        // log.error("Error generating QR code for {}: {}", email, e.getMessage());
        // }

        return secret;
    }

    @Override
    public String generateTotp(String secret) throws Exception {
        return codeGenerator.generate(secret, timeProvider.getTime());
    }

    /**
     * Verify the TOTP code sent by the user.
     */
    @Override
    public boolean verifyTotp(String secret, String code) {
        try {
            return codeVerifier.isValidCode(secret, code);
        } catch (Exception e) {
            log.error("Error verifying OTP {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String[] generateRecoveryCode() {
        var recoveryCodes = recoveryCodeGenerator.generateCodes(16);
        log.info("Generated recovery codes: {}", (Object) recoveryCodes);
        return recoveryCodes;
    }
}
