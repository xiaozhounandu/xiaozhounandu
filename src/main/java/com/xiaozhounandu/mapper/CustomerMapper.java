package com.xiaozhounandu.mapper;

import com.xiaozhounandu.entity.Customer;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface CustomerMapper {

   public List<Customer> selectAllCustomers();
}
