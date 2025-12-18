package com.xiaozhounandu.controller;

import com.xiaozhounandu.dto.response.ApiResult;
import com.xiaozhounandu.dto.response.PageResponse;
import com.xiaozhounandu.entity.LoginLog;
import com.xiaozhounandu.entity.OperationLog;
import com.xiaozhounandu.entity.User;
import com.xiaozhounandu.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = "*")
public class LogController {

    @Autowired
    private LogService logService;

    @GetMapping("/operations")
    public ApiResult<PageResponse<OperationLog>> operations(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime endDate,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {

        User currentUser = AuthController.getUserByToken(token);
        if (currentUser == null) {
            return ApiResult.error("未登录或token已过期");
        }

        // 只有ADMIN可以查看操作日志
        if (!"ADMIN".equals(currentUser.getRole())) {
            return ApiResult.error("无权查看操作日志");
        }

        PageResponse<OperationLog> result = logService.getOperationLogs(
                module, operation, username, startDate, endDate, page, pageSize
        );

        return ApiResult.success(result);
    }

    @GetMapping("/login")
    public ApiResult<PageResponse<LoginLog>> loginLogs(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer result,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDateTime endDate,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {

        User currentUser = AuthController.getUserByToken(token);
        if (currentUser == null) {
            return ApiResult.error("未登录或token已过期");
        }

        // 只有ADMIN可以查看登录日志
        if (!"ADMIN".equals(currentUser.getRole())) {
            return ApiResult.error("无权查看登录日志");
        }

        PageResponse<LoginLog> logResult = logService.getLoginLogs(
                username, result, startDate, endDate, page, pageSize
        );

        return ApiResult.success(logResult);
    }
}
