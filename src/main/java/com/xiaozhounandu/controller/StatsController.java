package com.xiaozhounandu.controller;

import com.xiaozhounandu.dto.response.ApiResult;
import com.xiaozhounandu.dto.response.DashboardResponse;
import com.xiaozhounandu.entity.User;
import com.xiaozhounandu.service.CustomerService;
import com.xiaozhounandu.service.FollowUpService;
import com.xiaozhounandu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "*")
public class StatsController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private FollowUpService followUpService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public ApiResult<DashboardResponse> dashboard(@RequestHeader("Authorization") String token) {
        User currentUser = AuthController.getUserByToken(token);
        if (currentUser == null) {
            return ApiResult.error("未登录或token已过期");
        }

        // 权限检查：只有ADMIN和MANAGER可以查看统计数据
        if (!"ADMIN".equals(currentUser.getRole()) && !"MANAGER".equals(currentUser.getRole())) {
            return ApiResult.error("无权查看统计数据");
        }

        DashboardResponse response = new DashboardResponse();
        response.setTotalCustomers(customerService.countTotalCustomers());
        response.setNewCustomers(customerService.countNewCustomers(30));
        response.setActiveCustomers(customerService.countActiveCustomers());
        response.setDealedCustomers(customerService.countDealedCustomers());
        response.setLostCustomers(customerService.countLostCustomers());
        response.setTodayFollowups(followUpService.countTodayFollowUps());
        response.setUpcomingFollowups(followUpService.countUpcomingFollowUps());
        response.setRecent7Days(customerService.getRecent7Days());
        response.setByIndustry(customerService.getByIndustry());
        response.setByLevel(customerService.getByLevel());
        response.setCustomerStatus(customerService.getCustomerStatus());
        response.setMonthlyTrend(customerService.getMonthlyTrend());
        response.setFollowUpByType(followUpService.getFollowUpByType());

        return ApiResult.success(response);
    }

    @GetMapping("/summary")
    public ApiResult<Map<String, Object>> summary(@RequestHeader("Authorization") String token) {
        User currentUser = AuthController.getUserByToken(token);
        if (currentUser == null) {
            return ApiResult.error("未登录或token已过期");
        }

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalUsers", userService.countTotalUsers());
        summary.put("totalCustomers", customerService.countTotalCustomers());
        summary.put("totalFollowUps", followUpService.countTotalFollowUps());
        summary.put("lastUpdate", LocalDateTime.now());

        return ApiResult.success(summary);
    }
}
