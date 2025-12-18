package com.xiaozhounandu.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerQueryRequest {
    private Integer page = 1;
    private Integer pageSize = 10;
    private String name;
    private String company;
    private Integer status;
    private String level;
    private Long ownerId;

    public Integer getOffset() {
        return (page - 1) * pageSize;
    }
}
