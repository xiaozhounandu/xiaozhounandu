package com.xiaozhounandu.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowUpRequest {
    private Long customerId;
    private String type;
    private String content;
    private String result;
    private String nextFollowTime;
}
