package com.xiaozhounandu.mapper;

import com.xiaozhounandu.entity.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CustomerMapper {
    // 分页查询
    List<Customer> selectByQuery(@Param("name") String name,
                                 @Param("company") String company,
                                 @Param("status") Integer status,
                                 @Param("level") String level,
                                 @Param("ownerId") Long ownerId,
                                 @Param("offset") Integer offset,
                                 @Param("pageSize") Integer pageSize);

    // 统计总数
    int countByQuery(@Param("name") String name,
                     @Param("company") String company,
                     @Param("status") Integer status,
                     @Param("level") String level,
                     @Param("ownerId") Long ownerId);

    // 根据ID查询
    Customer selectById(@Param("id") Long id);

    // 客户列表（关联查询用户信息）
    List<Customer> selectAllWithUser();

    // 新增
    int insert(Customer customer);

    // 更新
    int update(Customer customer);

    // 更新归属人
    int updateOwnerId(@Param("id") Long id, @Param("newOwnerId") Long newOwnerId);

    // 软删除 (更新status=0)
    int softDelete(@Param("id") Long id);

    // 删除
    int delete(@Param("id") Long id);

    // 统计最近days天新增客户
    int countNewCustomers(@Param("days") int days);

    // 获取最近7天新增客户分布
    List<Map<String, Object>> getRecent7Days();

    // 按行业统计
    List<Map<String, Object>> getByIndustry();

    // 按等级统计
    List<Map<String, Object>> getByLevel();
}
