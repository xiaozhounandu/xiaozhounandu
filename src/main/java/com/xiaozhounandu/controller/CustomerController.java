package com.xiaozhounandu.controller;

import com.xiaozhounandu.entity.Customer;
import com.xiaozhounandu.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/user")
    public List<Customer> getAllCustomers() {
        return customerService.selectAllCustomers();
    }


}
