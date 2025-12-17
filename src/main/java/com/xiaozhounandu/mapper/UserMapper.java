package com.xiaozhounandu.mapper;

import com.xiaozhounandu.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User findByUsername(@Param("username") String username);

    User findById(@Param("id") Long id);

    int insertUser(User user);

    int updateUser(User user);

    int deleteUserById(@Param("id") Long id);
}