package com.xiaozhounandu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {
    private Long totalCustomers;
    private Long newCustomers;
    private Long activeCustomers;
    private Long dealedCustomers;
    private Long lostCustomers;
    private Long todayFollowups;
    private Long upcomingFollowups;

    // 7天趋势数据
    private List<Map<String, Object>> recent7Days;

    // 行业分布数据 - Map格式
    private Map<String, Object> byIndustry;

    // 客户等级分布数据 - Map格式
    private Map<String, Object> byLevel;

    // 月度增长趋势数据
    private List<Map<String, Object>> monthlyTrend;

    // 跟进方式统计
    private Map<String, Object> followUpByType;

    // 客户状态分布
    private Map<String, Object> customerStatus;
}
