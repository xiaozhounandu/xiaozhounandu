package com.xiaozhounandu.mapper;

import com.xiaozhounandu.entity.FollowUp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface FollowUpMapper {
    // 获取客户跟进历史（分页）
    List<FollowUp> selectByCustomerId(@Param("customerId") Long customerId,
                                      @Param("offset") Integer offset,
                                      @Param("pageSize") Integer pageSize);

    // 统计客户跟进总数
    int countByCustomerId(@Param("customerId") Long customerId);

    // 新增
    int insert(FollowUp followUp);

    // 删除
    int delete(@Param("id") Long id);

    // 根据ID查询
    FollowUp selectById(@Param("id") Long id);

    // 获取最近几天的跟进
    List<FollowUp> selectRecentDays(@Param("days") Integer days);

    // 统计今日跟进数
    int countTodayFollowUps();

    // 统计即将跟进数(未来3天内)
    int countUpcomingFollowUps();

    // 统计跟进记录总数
    long countTotalFollowUps();

    // 按类型统计跟进记录
    List<Map<String, Object>> getFollowUpByType();
}
