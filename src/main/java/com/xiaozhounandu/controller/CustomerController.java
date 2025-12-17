package com.xiaozhounandu.controller;

import com.xiaozhounandu.entity.Customer;
import com.xiaozhounandu.entity.User;
import com.xiaozhounandu.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/user")
    public List<Customer> getAllCustomers(HttpServletRequest request) {
        // 获取当前登录用户
        User currentUser = (User) request.getAttribute("currentUser");
        System.out.println("当前用户: " + currentUser.getUsername() + ", 角色: " + currentUser.getRole());

        // 这里可以根据用户角色过滤数据，暂时返回所有客户
        return customerService.selectAllCustomers();
    }
//    @GetMapping("/id/{id}")
//    public Customer getAllCustomers(@PathVariable Integer id) {
//        return customerService.selectCustomerById(id);
//    }
//      http://localhost:8080/api/customers/id?id=1
    @GetMapping("/id")
    public Customer getCustomerById(@RequestParam Integer id) {
        return customerService.selectCustomerById(id);
    }

    @PostMapping("/insert")
    public int insertCustomer(@RequestBody Customer customer) {
        return customerService.insertCustomer(customer);
    }
    @DeleteMapping("/id1")
    public int deleteCustomerById(@RequestParam Integer id) {
        return customerService.deleteCustomerById(id);
    }
    @PostMapping("/id2")
    public int updateCustomer(@RequestBody Customer customer,@RequestParam Integer id ) {
        customer.setId(Long.valueOf(id));
        return customerService.updateCustomer(customer);
    }

}
