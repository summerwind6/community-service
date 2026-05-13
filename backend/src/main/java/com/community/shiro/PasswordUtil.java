package com.community.shiro;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class PasswordUtil {

    private static final int HASH_ITERATIONS = 2;

    public static String generateSalt() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String encrypt(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);
            byte[] sourceBytes = password.getBytes(StandardCharsets.UTF_8);

            md.update(saltBytes);
            byte[] hashed = md.digest(sourceBytes);

            for (int i = 1; i < HASH_ITERATIONS; i++) {
                md.reset();
                hashed = md.digest(hashed);
            }

            return bytesToHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不可用", e);
        }
    }

    public static boolean verify(String password, String salt, String hashedPassword) {
        String encrypt = encrypt(password, salt);
        return encrypt.equals(hashedPassword);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}
