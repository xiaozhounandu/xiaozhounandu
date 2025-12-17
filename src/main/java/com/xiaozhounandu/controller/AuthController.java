package com.xiaozhounandu.controller;

import com.xiaozhounandu.entity.User;
import com.xiaozhounandu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    // 存储session的简单内存存储（实际项目应使用Redis等）
    private static final Map<String, User> sessionStore = new HashMap<>();

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        Map<String, Object> result = new HashMap<>();

        if (username == null || password == null) {
            result.put("success", false);
            result.put("message", "用户名和密码不能为空");
            return result;
        }

        User user = userService.login(username, password);

        if (user != null) {
            // 生成token
            String token = UUID.randomUUID().toString();
            sessionStore.put(token, user);

            result.put("success", true);
            result.put("message", "登录成功");
            result.put("token", token);
            result.put("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "role", user.getRole()
            ));
        } else {
            result.put("success", false);
            result.put("message", "用户名或密码错误");
        }

        return result;
    }

    @PostMapping("/logout")
    public Map<String, Object> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> result = new HashMap<>();

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            sessionStore.remove(token);
        }

        result.put("success", true);
        result.put("message", "退出登录成功");
        return result;
    }

    @GetMapping("/user")
    public Map<String, Object> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, Object> result = new HashMap<>();

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            User user = sessionStore.get(token);

            if (user != null) {
                result.put("success", true);
                result.put("user", Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "role", user.getRole()
                ));
            } else {
                result.put("success", false);
                result.put("message", "用户未登录或token已过期");
            }
        } else {
            result.put("success", false);
            result.put("message", "未提供token");
        }

        return result;
    }

    public static User getUserByToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return sessionStore.get(token);
        }
        return null;
    }

    // 修复密码加密方法，使用与注册时相同的逻辑
    private String md5(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }
}