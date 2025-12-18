package com.xiaozhounandu.service;

import com.xiaozhounandu.dto.response.PageResponse;
import com.xiaozhounandu.entity.LoginLog;
import com.xiaozhounandu.entity.OperationLog;

import java.time.LocalDateTime;

public interface LogService {
    // 操作日志
    PageResponse<OperationLog> getOperationLogs(String module, String operation, String username,
                                               LocalDateTime startDate, LocalDateTime endDate,
                                               Integer page, Integer pageSize);

    // 登录日志
    PageResponse<LoginLog> getLoginLogs(String username, Integer result,
                                       LocalDateTime startDate, LocalDateTime endDate,
                                       Integer page, Integer pageSize);

    int addOperationLog(OperationLog log);

    int addLoginLog(LoginLog log);
}
