package com.community.shiro;

import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.util.ByteSource;

import java.util.UUID;

public class PasswordUtil {

    private static final int HASH_ITERATIONS = 2;

    public static String generateSalt() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String encrypt(String password, String salt) {
        return new Md5Hash(password, salt, HASH_ITERATIONS).toHex();
    }

    public static boolean verify(String password, String salt, String hashedPassword) {
        String encrypt = encrypt(password, salt);
        return encrypt.equals(hashedPassword);
    }
}
