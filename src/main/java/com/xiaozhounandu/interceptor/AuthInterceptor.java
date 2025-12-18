package com.xiaozhounandu.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaozhounandu.controller.AuthController;
import com.xiaozhounandu.dto.response.ApiResult;
import com.xiaozhounandu.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        String path = request.getRequestURI();

        // 允许公开访问
        if (path.contains("/api/auth/login") ||
            path.contains("/api/auth/register") ||
            path.contains("/api/test") ||
            path.contains("/api/init/")) {
            return true;
        }

        String token = request.getHeader("Authorization");

        if (token == null) {
            response.setStatus(401);
            response.getWriter().write(objectMapper.writeValueAsString(
                ApiResult.error("请先登录")
            ));
            return false;
        }

        User user = AuthController.getUserByToken(token);
        if (user == null) {
            response.setStatus(401);
            response.getWriter().write(objectMapper.writeValueAsString(
                ApiResult.error("token无效或已过期")
            ));
            return false;
        }

        request.setAttribute("currentUser", user);
        return true;
    }
}
