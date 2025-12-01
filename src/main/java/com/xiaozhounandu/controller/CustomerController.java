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
