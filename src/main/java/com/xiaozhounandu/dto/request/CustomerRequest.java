package com.xiaozhounandu.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequest {
    private String name;
    private String company;
    private String phone;
    private String email;
    private String industry;
    private String position;
    private String address;
    private String source;
    private Integer status;
    private String level;
    private String remark;
}
