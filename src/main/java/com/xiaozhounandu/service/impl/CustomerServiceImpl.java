package com.xiaozhounandu.service.impl;

import com.xiaozhounandu.dto.request.CustomerQueryRequest;
import com.xiaozhounandu.dto.response.PageResponse;
import com.xiaozhounandu.entity.Customer;
import com.xiaozhounandu.mapper.CustomerMapper;
import com.xiaozhounandu.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public PageResponse<Customer> getCustomerList(CustomerQueryRequest query) {
        int total = customerMapper.countByQuery(
                query.getName(),
                query.getCompany(),
                query.getStatus(),
                query.getLevel(),
                query.getOwnerId()
        );

        List<Customer> list = new ArrayList<>();
        if (total > 0) {
            list = customerMapper.selectByQuery(
                    query.getName(),
                    query.getCompany(),
                    query.getStatus(),
                    query.getLevel(),
                    query.getOwnerId(),
                    query.getOffset(),
                    query.getPageSize()
            );
        }

        int pages = (total + query.getPageSize() - 1) / query.getPageSize();

        return PageResponse.<Customer>builder()
                .list(list)
                .total((long) total)
                .page(query.getPage())
                .pageSize(query.getPageSize())
                .pages(pages)
                .build();
    }

    @Override
    public Customer getCustomerById(Long id) {
        return customerMapper.selectById(id);
    }

    @Override
    public Customer getCustomerByIdWithDetail(Long id) {
        return customerMapper.selectById(id);
    }

    @Override
    public int addCustomer(Customer customer) {
        customer.setStatus(1); // 默认正常状态
        return customerMapper.insert(customer);
    }

    @Override
    public int updateCustomer(Long id, Customer customer) {
        customer.setId(id);
        return customerMapper.update(customer);
    }

    @Override
    public int deleteCustomer(Long id) {
        return customerMapper.softDelete(id);
    }

    @Override
    public int transferCustomer(Long id, Long newOwnerId) {
        return customerMapper.updateOwnerId(id, newOwnerId);
    }

    @Override
    public Long countTotalCustomers() {
        return (long) customerMapper.countByQuery(null, null, null, null, null);
    }

    @Override
    public Long countNewCustomers(int days) {
        return (long) customerMapper.countNewCustomers(days);
    }

    @Override
    public Long countActiveCustomers() {
        return (long) customerMapper.countByQuery(null, null, 1, null, null);
    }

    @Override
    public Long countDealedCustomers() {
        return (long) customerMapper.countByQuery(null, null, 2, null, null);
    }

    @Override
    public Long countLostCustomers() {
        return (long) customerMapper.countByQuery(null, null, 3, null, null);
    }

    @Override
    public List<Map<String, Object>> getRecent7Days() {
        return customerMapper.getRecent7Days();
    }

    @Override
    public List<Map<String, Object>> getByIndustry() {
        return customerMapper.getByIndustry();
    }

    @Override
    public List<Map<String, Object>> getByLevel() {
        return customerMapper.getByLevel();
    }
}
