package me.oreos.iam.services.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordHasher {
    private static final int SALT_SIZE = 16;
    private static final int ITERATIONS = 100000;
    private static final int KEY_SIZE = 32;

    public static String hashPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = new byte[SALT_SIZE];
        new SecureRandom().nextBytes(salt);

        byte[] hash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_SIZE * 8);

        byte[] hashBytes = new byte[SALT_SIZE + hash.length];
        System.arraycopy(salt, 0, hashBytes, 0, SALT_SIZE);
        System.arraycopy(hash, 0, hashBytes, SALT_SIZE, hash.length);

        return Base64.getEncoder().encodeToString(hashBytes);
    }

    public static boolean verifyPassword(String password, String hashedPassword)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] hashBytes = Base64.getDecoder().decode(hashedPassword);

        byte[] salt = Arrays.copyOfRange(hashBytes, 0, SALT_SIZE);
        byte[] storedHash = Arrays.copyOfRange(hashBytes, SALT_SIZE, hashBytes.length);

        byte[] computedHash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_SIZE * 8);

        return Arrays.equals(computedHash, storedHash);
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return skf.generateSecret(spec).getEncoded();
    }
}
