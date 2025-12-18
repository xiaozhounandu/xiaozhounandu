package com.xiaozhounandu.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginLog {
    private Long id;
    private Long userId;
    private String username;
    private String ipAddress;
    private String device;
    private String location;
    private Integer result; // 0-失败, 1-成功
    private String errorMsg;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
