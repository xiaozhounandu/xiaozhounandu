    package com.xiaozhounandu.entity;

    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.time.LocalDateTime;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class BaseEntity {
        //     对应数据库字段: owner_id
        private Long ownerId;

        // 对应数据库字段: create_time
        private String createTime;

        // 对应数据库字段: update_time
//        private LocalDateTime updateTime;
        private String updateTime;

    }
