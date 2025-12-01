package com.xiaozhounandu.service;

import com.xiaozhounandu.entity.Customer;
import com.xiaozhounandu.mapper.CustomerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    @Autowired
    private CustomerMapper customerMapper;

    public List<Customer> selectAllCustomers() {
        return customerMapper.selectAllCustomers();
    }

    public Customer selectCustomerById(Integer id) {
        return customerMapper.selectCustomerById(id);
    }

    public int  insertCustomer(Customer customer) {
        LocalDate today = LocalDate.now();
        customer.setCreateTime(today);
        customer.setUpdateTime(today);
        return customerMapper.insertCustomer(customer);
    }

    public int deleteCustomerById(Integer id) {
            return   customerMapper.deleteCustomerById(id);

    }

    public int updateCustomer(Customer customer) {
        // 1. 先查询出旧的客户数据，以获取原始的 createTime
        Customer oldCustomer = customerMapper.selectCustomerById(Math.toIntExact(customer.getId()));

        // 2. 将原始的创建时间设置回传入的对象中，防止其被更新为 null 或新日期
        customer.setCreateTime(oldCustomer.getCreateTime());

        // 3. 设置 updateTime 为当前日期
        customer.setUpdateTime(LocalDate.now());

        return customerMapper.updateCustomer(customer);
    }
}

