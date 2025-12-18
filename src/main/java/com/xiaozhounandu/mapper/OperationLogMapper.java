package com.xiaozhounandu.mapper;

import com.xiaozhounandu.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OperationLogMapper {
    // 分页查询
    List<OperationLog> selectByQuery(@Param("module") String module,
                                     @Param("operation") String operation,
                                     @Param("username") String username,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate,
                                     @Param("offset") Integer offset,
                                     @Param("pageSize") Integer pageSize);

    // 统计总数
    int countByQuery(@Param("module") String module,
                     @Param("operation") String operation,
                     @Param("username") String username,
                     @Param("startDate") LocalDateTime startDate,
                     @Param("endDate") LocalDateTime endDate);

    // 新增
    int insert(OperationLog log);
}
