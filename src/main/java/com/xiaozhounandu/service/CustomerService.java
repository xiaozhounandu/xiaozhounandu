package com.xiaozhounandu.service;

import com.xiaozhounandu.entity.Customer;
import com.xiaozhounandu.mapper.CustomerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    @Autowired
    private CustomerMapper customerMapper;

    public List<Customer> selectAllCustomers () {
        return customerMapper.selectAllCustomers();
    }

    public Customer selectCustomerById (Integer id) {
        return customerMapper.selectCustomerById(id);
    }

    public Customer insertCustomer(Customer customer){
        return customerMapper.insertCustomer(customer);
    }
}
