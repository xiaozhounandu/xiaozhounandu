package com.xiaozhounandu.service.impl;

import com.xiaozhounandu.dto.response.PageResponse;
import com.xiaozhounandu.entity.LoginLog;
import com.xiaozhounandu.entity.OperationLog;
import com.xiaozhounandu.mapper.LoginLogMapper;
import com.xiaozhounandu.mapper.OperationLogMapper;
import com.xiaozhounandu.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Autowired
    private LoginLogMapper loginLogMapper;

    @Override
    public PageResponse<OperationLog> getOperationLogs(String module, String operation, String username,
                                                      LocalDateTime startDate, LocalDateTime endDate,
                                                      Integer page, Integer pageSize) {
        int total = operationLogMapper.countByQuery(module, operation, username, startDate, endDate);
        List<OperationLog> list = new ArrayList<>();

        if (total > 0) {
            int offset = (page - 1) * pageSize;
            list = operationLogMapper.selectByQuery(module, operation, username, startDate, endDate, offset, pageSize);
        }

        int pages = (total + pageSize - 1) / pageSize;

        return PageResponse.<OperationLog>builder()
                .list(list)
                .total((long) total)
                .page(page)
                .pageSize(pageSize)
                .pages(pages)
                .build();
    }

    @Override
    public PageResponse<LoginLog> getLoginLogs(String username, Integer result,
                                              LocalDateTime startDate, LocalDateTime endDate,
                                              Integer page, Integer pageSize) {
        int total = loginLogMapper.countByQuery(username, result, startDate, endDate);
        List<LoginLog> list = new ArrayList<>();

        if (total > 0) {
            int offset = (page - 1) * pageSize;
            list = loginLogMapper.selectByQuery(username, result, startDate, endDate, offset, pageSize);
        }

        int pages = (total + pageSize - 1) / pageSize;

        return PageResponse.<LoginLog>builder()
                .list(list)
                .total((long) total)
                .page(page)
                .pageSize(pageSize)
                .pages(pages)
                .build();
    }

    @Override
    public int addOperationLog(OperationLog log) {
        return operationLogMapper.insert(log);
    }

    @Override
    public int addLoginLog(LoginLog log) {
        return loginLogMapper.insert(log);
    }
}
