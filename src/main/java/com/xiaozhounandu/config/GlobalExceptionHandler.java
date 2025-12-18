package com.xiaozhounandu.config;

import com.xiaozhounandu.dto.response.ApiResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ApiResult<String> handleException(Exception e) {
        e.printStackTrace();
        return ApiResult.error("系统内部错误: " + e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ApiResult<String> handleRuntimeException(RuntimeException e) {
        e.printStackTrace();
        return ApiResult.error(e.getMessage());
    }
}
