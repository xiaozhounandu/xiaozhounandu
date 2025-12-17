package com.xiaozhounandu.controller;

import com.xiaozhounandu.entity.User;
import com.xiaozhounandu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/init")
public class InitController {

    @Autowired
    private UserService userService;

    @GetMapping("/status")
    public Map<String, Object> checkInitStatus() {
        Map<String, Object> result = new HashMap<>();

        try {
            User admin = userService.findByUsername("admin");
            User user = userService.findByUsername("user");

            result.put("adminExists", admin != null);
            result.put("userExists", user != null);
            result.put("success", true);

            if (admin != null) {
                result.put("adminInfo", Map.of(
                    "id", admin.getId(),
                    "username", admin.getUsername(),
                    "role", admin.getRole(),
                    "status", admin.getStatus()
                ));
            }

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    @PostMapping("/create-users")
    public Map<String, Object> createDefaultUsers() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 检查admin用户是否存在
            User existingAdmin = userService.findByUsername("admin");
            if (existingAdmin == null) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword("admin123");
                admin.setEmail("admin@example.com");
                admin.setRole("ADMIN");
                admin.setStatus(1);

                int adminResult = userService.register(admin);
                result.put("adminCreated", adminResult > 0);
            } else {
                result.put("adminCreated", false);
                result.put("adminExists", true);
            }

            // 检查普通用户是否存在
            User existingUser = userService.findByUsername("user");
            if (existingUser == null) {
                User user = new User();
                user.setUsername("user");
                user.setPassword("user123");
                user.setEmail("user@example.com");
                user.setRole("USER");
                user.setStatus(1);

                int userResult = userService.register(user);
                result.put("userCreated", userResult > 0);
            } else {
                result.put("userCreated", false);
                result.put("userExists", true);
            }

            result.put("success", true);
            result.put("message", "用户初始化完成");

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    @GetMapping("/md5-test")
    public Map<String, Object> testMd5() {
        Map<String, Object> result = new HashMap<>();

        try {
            result.put("admin123", md5("admin123"));
            result.put("user123", md5("user123"));
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    private String md5(String input) {
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