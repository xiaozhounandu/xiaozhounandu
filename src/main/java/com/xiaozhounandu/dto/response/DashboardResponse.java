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
    private List<Map<String, Object>> recent7Days;
    private List<Map<String, Object>> byIndustry;
    private List<Map<String, Object>> byLevel;
}
