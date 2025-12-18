package com.xiaozhounandu.mapper;

import com.xiaozhounandu.entity.LoginLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface LoginLogMapper {
    // 分页查询
    List<LoginLog> selectByQuery(@Param("username") String username,
                                 @Param("result") Integer result,
                                 @Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate,
                                 @Param("offset") Integer offset,
                                 @Param("pageSize") Integer pageSize);

    // 统计总数
    int countByQuery(@Param("username") String username,
                     @Param("result") Integer result,
                     @Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate);

    // 新增
    int insert(LoginLog log);
}
