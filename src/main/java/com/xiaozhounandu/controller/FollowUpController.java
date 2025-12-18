package com.xiaozhounandu.controller;

import com.xiaozhounandu.dto.request.FollowUpRequest;
import com.xiaozhounandu.dto.response.ApiResult;
import com.xiaozhounandu.dto.response.PageResponse;
import com.xiaozhounandu.entity.Customer;
import com.xiaozhounandu.entity.FollowUp;
import com.xiaozhounandu.entity.User;
import com.xiaozhounandu.service.CustomerService;
import com.xiaozhounandu.service.FollowUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/follow-ups")
@CrossOrigin(origins = "*")
public class FollowUpController {

    @Autowired
    private FollowUpService followUpService;

    @Autowired
    private CustomerService customerService;

    @GetMapping("/customer/{customerId}")
    public ApiResult<PageResponse<FollowUp>> list(@RequestHeader("Authorization") String token,
                                                  @PathVariable Long customerId,
                                                  @RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer pageSize) {
        User currentUser = AuthController.getUserByToken(token);
        if (currentUser == null) {
            return ApiResult.error("未登录或token已过期");
        }

        // 验证客户存在且有权限
        Customer customer = customerService.getCustomerById(customerId);
        if (customer == null) {
            return ApiResult.error("客户不存在");
        }

        if (!hasPermission(customer, currentUser)) {
            return ApiResult.error("无权查看该客户的跟进记录");
        }

        PageResponse<FollowUp> result = followUpService.getFollowUpList(customerId, page, pageSize);
        return ApiResult.success(result);
    }

    @PostMapping
    public ApiResult<Long> create(@RequestHeader("Authorization") String token,
                                  @RequestBody FollowUpRequest request) {
        User currentUser = AuthController.getUserByToken(token);
        if (currentUser == null) {
            return ApiResult.error("未登录或token已过期");
        }

        // 验证客户存在且有权限
        Customer customer = customerService.getCustomerById(request.getCustomerId());
        if (customer == null) {
            return ApiResult.error("客户不存在");
        }

        if (!hasPermission(customer, currentUser)) {
            return ApiResult.error("无权对该客户添加跟进");
        }

        FollowUp followUp = new FollowUp();
        followUp.setCustomerId(request.getCustomerId());
        followUp.setFollowerId(currentUser.getId());
        followUp.setType(request.getType());
        followUp.setContent(request.getContent());
        followUp.setResult(request.getResult());

        // 解析时间字符串为LocalDateTime
        // followUp.setNextFollowTime(request.getNextFollowTime());

        int result = followUpService.addFollowUp(followUp);

        if (result > 0) {
            return ApiResult.success("跟进记录添加成功", followUp.getId());
        } else {
            return ApiResult.error("跟进记录添加失败");
        }
    }

    @DeleteMapping("/{id}")
    public ApiResult<String> delete(@RequestHeader("Authorization") String token,
                                    @PathVariable Long id) {
        User currentUser = AuthController.getUserByToken(token);
        if (currentUser == null) {
            return ApiResult.error("未登录或token已过期");
        }

        // 验证跟进记录存在
        FollowUp followUp = followUpService.getFollowUpById(id);
        if (followUp == null) {
            return ApiResult.error("跟进记录不存在");
        }

        // 验证客户权限
        Customer customer = customerService.getCustomerById(followUp.getCustomerId());
        if (customer == null) {
            return ApiResult.error("客户不存在");
        }

        if (!hasPermission(customer, currentUser) && !"ADMIN".equals(currentUser.getRole())) {
            return ApiResult.error("无权删除该跟进记录");
        }

        int result = followUpService.deleteFollowUp(id);

        if (result > 0) {
            return ApiResult.success("跟进记录删除成功");
        } else {
            return ApiResult.error("跟进记录删除失败");
        }
    }

    private boolean hasPermission(Customer customer, User currentUser) {
        if ("ADMIN".equals(currentUser.getRole()) || "MANAGER".equals(currentUser.getRole())) {
            return true;
        }
        return customer.getOwnerId().equals(currentUser.getId());
    }
}
