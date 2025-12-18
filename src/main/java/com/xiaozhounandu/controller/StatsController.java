package com.xiaozhounandu.controller;

import com.xiaozhounandu.dto.response.ApiResult;
import com.xiaozhounandu.dto.response.DashboardResponse;
import com.xiaozhounandu.entity.User;
import com.xiaozhounandu.service.CustomerService;
import com.xiaozhounandu.service.FollowUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

        return ApiResult.success(response);
    }
}
