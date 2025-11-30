package com.xiaozhounandu.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer  extends BaseEntity {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private String industry;
    private String address;



}
