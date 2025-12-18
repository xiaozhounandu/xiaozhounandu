package com.xiaozhounandu.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptionUtil {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // BCrypt加密
    public static String bcryptEncode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    // BCrypt验证
    public static boolean bcryptMatches(String rawPassword, String encodedPassword) {
        // 检查是否为有效的BCrypt格式，避免警告日志
        if (encodedPassword == null || encodedPassword.length() < 60) {
            return false;
        }
        // BCrypt哈希以$2a$、$2b$或$2y$开头
        if (!encodedPassword.startsWith("$2a$") && !encodedPassword.startsWith("$2b$") && !encodedPassword.startsWith("$2y$")) {
            return false;
        }
        return encoder.matches(rawPassword, encodedPassword);
    }

    // MD5加密 (兼容旧数据)
    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }
}
