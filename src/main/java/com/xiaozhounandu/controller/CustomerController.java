package com.xiaozhounandu.controller;

import com.xiaozhounandu.dto.request.CustomerQueryRequest;
import com.xiaozhounandu.dto.request.CustomerRequest;
import com.xiaozhounandu.dto.request.TransferRequest;
import com.xiaozhounandu.dto.response.ApiResult;
import com.xiaozhounandu.dto.response.CustomerDetailResponse;
import com.xiaozhounandu.dto.response.PageResponse;
import com.xiaozhounandu.entity.Customer;
import com.xiaozhounandu.entity.FollowUp;
import com.xiaozhounandu.entity.User;
import com.xiaozhounandu.service.CustomerService;
import com.xiaozhounandu.service.FollowUpService;
import com.xiaozhounandu.util.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private FollowUpService followUpService;

    @GetMapping
    public ApiResult<PageResponse<Customer>> list(@RequestHeader(value = "Authorization", required = false) String token,
                                                  CustomerQueryRequest query,
                                                  HttpServletRequest request) {
        User currentUser = AuthController.getUserByToken(token);
        if (currentUser == null) {
            return ApiResult.error("未登录或token已过期");
        }

        // 如果不是ADMIN，只能看自己的客户
        if (!"ADMIN".equals(currentUser.getRole()) && !"MANAGER".equals(currentUser.getRole())) {
            query.setOwnerId(currentUser.getId());
        }

        PageResponse<Customer> result = customerService.getCustomerList(query);
        return ApiResult.success(result);
    }

    @GetMapping("/{id}")
    public ApiResult<CustomerDetailResponse> detail(@RequestHeader("Authorization") String token,
                                                    @PathVariable Long id) {
        User currentUser = AuthController.getUserByToken(token);
        if (currentUser == null) {
            return ApiResult.error("未登录或token已过期");
        }

        Customer customer = customerService.getCustomerById(id);
        if (customer == null) {
            return ApiResult.error("客户不存在");
        }

        // 权限检查
        if (!hasPermission(customer, currentUser)) {
            return ApiResult.error("无权查看该客户");
        }

        // 获取跟进记录
        PageResponse<FollowUp> followUpPage = followUpService.getFollowUpList(id, 1, 100);

        CustomerDetailResponse response = new CustomerDetailResponse(customer, followUpPage.getList());
        return ApiResult.success(response);
    }

    @PostMapping
    public ApiResult<Long> create(@RequestHeader("Authorization") String token,
                                  @RequestBody CustomerRequest request,
                                  HttpServletRequest httpRequest) {
        User currentUser = AuthController.getUserByToken(token);
        if (currentUser == null) {
            return ApiResult.error("未登录或token已过期");
        }

        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setCompany(request.getCompany());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setIndustry(request.getIndustry());
        customer.setPosition(request.getPosition());
        customer.setAddress(request.getAddress());
        customer.setSource(request.getSource());
        customer.setStatus(1);
        customer.setLevel(request.getLevel());
        customer.setOwnerId(currentUser.getId());
        customer.setCreatorId(currentUser.getId());
        customer.setRemark(request.getRemark());

        int result = customerService.addCustomer(customer);

        if (result > 0) {
            // 记录操作日志
            // logOperation(currentUser, "CUSTOMER", "CREATE", customer.getId(), customer.getName(), null, httpRequest);
            return ApiResult.success("客户新增成功", customer.getId());
        } else {
            return ApiResult.error("客户新增失败");
        }
    }

    @PutMapping("/{id}")
    public ApiResult<String> update(@RequestHeader("Authorization") String token,
                                    @PathVariable Long id,
                                    @RequestBody CustomerRequest request) {
        User currentUser = AuthController.getUserByToken(token);
        if (currentUser == null) {
            return ApiResult.error("未登录或token已过期");
        }

        Customer oldCustomer = customerService.getCustomerById(id);
        if (oldCustomer == null) {
            return ApiResult.error("客户不存在");
        }

        // 权限检查：只能修改自己的客户，除非是ADMIN
        if (!hasPermission(oldCustomer, currentUser)) {
            return ApiResult.error("无权修改该客户");
        }

        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setCompany(request.getCompany());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setIndustry(request.getIndustry());
        customer.setPosition(request.getPosition());
        customer.setAddress(request.getAddress());
        customer.setSource(request.getSource());
        customer.setStatus(request.getStatus());
        customer.setLevel(request.getLevel());
        customer.setRemark(request.getRemark());

        int result = customerService.updateCustomer(id, customer);

        if (result > 0) {
            return ApiResult.success("客户更新成功");
        } else {
            return ApiResult.error("客户更新失败");
        }
    }

    @DeleteMapping("/{id}")
    public ApiResult<String> delete(@RequestHeader("Authorization") String token,
                                    @PathVariable Long id) {
        User currentUser = AuthController.getUserByToken(token);
        if (currentUser == null) {
            return ApiResult.error("未登录或token已过期");
        }

        Customer customer = customerService.getCustomerById(id);
        if (customer == null) {
            return ApiResult.error("客户不存在");
        }

        // 权限检查：只有ADMIN可以删除
        if (!"ADMIN".equals(currentUser.getRole())) {
            return ApiResult.error("只有管理员可以删除客户");
        }

        int result = customerService.deleteCustomer(id);

        if (result > 0) {
            return ApiResult.success("客户删除成功");
        } else {
            return ApiResult.error("客户删除失败");
        }
    }

    @PutMapping("/{id}/transfer")
    public ApiResult<String> transfer(@RequestHeader("Authorization") String token,
                                      @PathVariable Long id,
                                      @RequestBody TransferRequest request) {
        User currentUser = AuthController.getUserByToken(token);
        if (currentUser == null) {
            return ApiResult.error("未登录或token已过期");
        }

        Customer customer = customerService.getCustomerById(id);
        if (customer == null) {
            return ApiResult.error("客户不存在");
        }

        // 权限检查：只有ADMIN或原归属人可以转移
        if (!"ADMIN".equals(currentUser.getRole()) &&
            !customer.getOwnerId().equals(currentUser.getId())) {
            return ApiResult.error("无权转移该客户");
        }

        int result = customerService.transferCustomer(id, request.getNewOwnerId());

        if (result > 0) {
            return ApiResult.success("客户转移成功");
        } else {
            return ApiResult.error("客户转移失败");
        }
    }

    // 权限检查辅助方法
    private boolean hasPermission(Customer customer, User currentUser) {
        // ADMIN、MANAGER可以查看所有
        if ("ADMIN".equals(currentUser.getRole()) || "MANAGER".equals(currentUser.getRole())) {
            return true;
        }
        // 普通用户只能看自己的
        return customer.getOwnerId().equals(currentUser.getId());
    }
}
