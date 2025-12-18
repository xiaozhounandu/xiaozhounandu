package com.xiaozhounandu.controller;

import com.xiaozhounandu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private UserService userService;

    @GetMapping("/db")
    public Map<String, Object> testDatabase() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 测试数据库连接
            var admin = userService.findByUsername("admin");
            if (admin != null) {
                result.put("success", true);
                result.put("message", "数据库连接正常");
                result.put("adminUser", Map.of(
                    "id", admin.getId(),
                    "username", admin.getUsername(),
                    "role", admin.getRole()
                ));
            } else {
                result.put("success", false);
                result.put("message", "未找到admin用户，请先执行数据库脚本");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "数据库连接失败: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }
}