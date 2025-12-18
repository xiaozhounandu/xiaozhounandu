package com.xiaozhounandu;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Main {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 生成 admin123 的BCrypt hash
        String password = "admin123";
        String hash = encoder.encode(password);

        System.out.println("密码: " + password);
        System.out.println("BCrypt Hash: " + hash);
        System.out.println("长度: " + hash.length());

        // 验证数据库中的hash
        String dbHash = "$2a$10$N.ZmdGzV7IOTH8w2j7C8rKeNW8pC8t9q.WO2vPn6lzK5cB4eS7R0Cq";
        boolean matches = encoder.matches(password, dbHash);
        System.out.println("\n数据库hash是否匹配admin123: " + matches);

        // 说明
        if (!matches) {
            System.out.println("\n=== 问题找到了 ===");
            System.out.println("数据库中的hash不匹配admin123！");
            System.out.println("数据库中的user用户密码也不正确！");
            System.out.println("\n请在数据库执行以下SQL更新密码:");
            System.out.println("UPDATE user SET password = '" + encoder.encode("admin123") + "' WHERE username = 'admin';");
            System.out.println("UPDATE user SET password = '" + encoder.encode("admin123") + "' WHERE username = 'manager';");
            System.out.println("UPDATE user SET password = '" + encoder.encode("user123") + "' WHERE username = 'user';");
        }
    }
}
