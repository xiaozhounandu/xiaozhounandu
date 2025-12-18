package com.xiaozhounandu.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.xiaozhounandu.mapper")
public class MyBatisConfig {
    // MyBatis配置已在application.yml中设置
}
