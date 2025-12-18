package com.xiaozhounandu.service;

import com.xiaozhounandu.dto.request.CustomerQueryRequest;
import com.xiaozhounandu.dto.response.PageResponse;
import com.xiaozhounandu.entity.Customer;

import java.util.List;
import java.util.Map;

public interface CustomerService {
    PageResponse<Customer> getCustomerList(CustomerQueryRequest query);

    Customer getCustomerById(Long id);

    Customer getCustomerByIdWithDetail(Long id);

    int addCustomer(Customer customer);

    int updateCustomer(Long id, Customer customer);

    int deleteCustomer(Long id);

    int transferCustomer(Long id, Long newOwnerId);

    // 统计数据
    Long countTotalCustomers();

    Long countNewCustomers(int days);

    Long countActiveCustomers();

    Long countDealedCustomers();

    Long countLostCustomers();

    List<Map<String, Object>> getRecent7Days();

    Map<String, Object> getByIndustry();

    Map<String, Object> getByLevel();

    Map<String, Object> getCustomerStatus();

    List<Map<String, Object>> getMonthlyTrend();
}
