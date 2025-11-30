package com.xiaozhounandu;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@MapperScan("com.xiaozhounandu.mapper")

public class XiaozhounanduApplication {

    public static void main(String[] args) {
        SpringApplication.run(XiaozhounanduApplication.class, args);
    }

}
