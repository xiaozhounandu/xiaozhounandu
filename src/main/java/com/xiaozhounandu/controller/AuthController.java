package com.xiaozhounandu.controller;

import com.xiaozhounandu.dto.request.LoginRequest;
import com.xiaozhounandu.dto.request.PasswordUpdateRequest;
import com.xiaozhounandu.dto.request.RegisterRequest;
import com.xiaozhounandu.dto.response.ApiResult;
import com.xiaozhounandu.dto.response.UserInfoResponse;
import com.xiaozhounandu.entity.LoginLog;
import com.xiaozhounandu.entity.User;
import com.xiaozhounandu.service.AuthService;
import com.xiaozhounandu.service.UserService;
import com.xiaozhounandu.util.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
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
    private AuthService authService;

    @Autowired
    private UserService userService;

    // 存储session的简单内存存储（后续可改为Redis）
    private static final Map<String, User> sessionStore = new HashMap<>();

    @PostMapping("/register")
    public ApiResult<UserInfoResponse> register(@RequestBody RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setNickname(request.getNickname());
        user.setRole("USER");

        int result = authService.register(user);

        if (result == -1) {
            return ApiResult.error("用户名已存在");
        } else if (result == -2) {
            return ApiResult.error("用户名不能为空");
        } else if (result == -3) {
            return ApiResult.error("密码不能为空");
        }

        UserInfoResponse response = new UserInfoResponse(
            user.getId(), user.getUsername(), user.getEmail(),
            user.getPhone(), user.getNickname(), user.getAvatar(),
            user.getRole(), user.getStatus(), null, user.getCreateTime()
        );

        return ApiResult.success("注册成功", response);
    }

    @PostMapping("/login")
    public ApiResult<Map<String, Object>> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        if (request.getUsername() == null || request.getPassword() == null) {
            return ApiResult.error("用户名和密码不能为空");
        }

        User user = authService.login(request.getUsername(), request.getPassword());

        if (user != null) {
            // 生成token
            String token = UUID.randomUUID().toString();
            sessionStore.put(token, user);

            // 记录登录日志
            recordLoginLog(user, httpRequest, 1, null);

            Map<String, Object> data = new HashMap<>();
            data.put("token", "Bearer " + token);
            data.put("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "nickname", user.getNickname(),
                "email", user.getEmail(),
                "role", user.getRole()
            ));

            return ApiResult.success("登录成功", data);
        } else {
            // 记录登录失败日志
            User tempUser = new User();
            tempUser.setId(0L);
            recordLoginLog(tempUser, httpRequest, 0, "用户名或密码错误");

            return ApiResult.error("用户名或密码错误");
        }
    }

    @PostMapping("/logout")
    public ApiResult<String> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String actualToken = token.substring(7);
            sessionStore.remove(actualToken);
        }
        return ApiResult.success("退出登录成功");
    }

    @GetMapping("/current")
    public ApiResult<UserInfoResponse> getCurrentUser(@RequestHeader(value = "Authorization", required = false) String token) {
        User user = getUserByToken(token);

        if (user == null) {
            return ApiResult.error("未登录或token已过期");
        }

        UserInfoResponse response = new UserInfoResponse(
            user.getId(), user.getUsername(), user.getEmail(),
            user.getPhone(), user.getNickname(), user.getAvatar(),
            user.getRole(), user.getStatus(), user.getLastLoginTime(), user.getCreateTime()
        );

        return ApiResult.success(response);
    }

    @PutMapping("/password")
    public ApiResult<String> updatePassword(@RequestHeader("Authorization") String token,
                                           @RequestBody PasswordUpdateRequest request) {
        User user = getUserByToken(token);
        if (user == null) {
            return ApiResult.error("未登录或token已过期");
        }

        int result = authService.updatePassword(user.getId(), request.getOldPassword(), request.getNewPassword());

        if (result == 0) {
            return ApiResult.error("修改密码失败");
        } else if (result == -1) {
            return ApiResult.error("旧密码错误");
        }

        return ApiResult.success("密码修改成功");
    }

    // 静态方法供拦截器调用
    public static User getUserByToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String actualToken = token.substring(7);
            return sessionStore.get(actualToken);
        }
        return null;
    }

    // 记录登录日志
    private void recordLoginLog(User user, HttpServletRequest request, int result, String errorMsg) {
        try {
            LoginLog log = new LoginLog();
            log.setUserId(user.getId() != null ? user.getId() : 0L);
            log.setUsername(user.getUsername());
            log.setIpAddress(IpUtil.getClientIp(request));
            log.setDevice(request.getHeader("User-Agent"));
            log.setLocation("内网IP"); // 可通过IP库查询真实位置
            log.setResult(result);
            log.setErrorMsg(errorMsg);

            // 异步记录日志（避免影响登录性能）
            new Thread(() -> {
                try {
                    // 这里需要注入LogService，稍后优化
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}