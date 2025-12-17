package com.xiaozhounandu.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaozhounandu.controller.AuthController;
import com.xiaozhounandu.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 设置响应内容类型
        response.setContentType("application/json;charset=UTF-8");

        // 获取请求路径
        String path = request.getRequestURI();

        // 允许的路径（不需要认证）
        if (path.contains("/api/auth/login") ||
            path.contains("/api/auth/register")) {
            return true;
        }

        // 获取Authorization header
        String token = request.getHeader("Authorization");

        if (token == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "请先登录");
            response.setStatus(401);
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            return false;
        }

        // 验证token
        User user = AuthController.getUserByToken(token);
        if (user == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "token无效或已过期");
            response.setStatus(401);
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            return false;
        }

        // 将用户信息存入request，方便后续使用
        request.setAttribute("currentUser", user);
        return true;
    }
}