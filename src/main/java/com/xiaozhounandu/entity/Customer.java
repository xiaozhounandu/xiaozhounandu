package com.xiaozhounandu.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.xiaozhounandu.deserializer.CustomerStatusDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    private Long id;
    private String name;
    private String company;
    private String phone;
    private String email;
    private String industry;
    private String position;
    private String address;
    private String source;
    @JsonDeserialize(using = CustomerStatusDeserializer.class)
    private Integer status; // 0-删除, 1-正常, 2-已成交, 3-流失
    private String level; // A/B/C/D
    private Long ownerId;
    private Long creatorId;
    private String remark;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime nextFollowTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
