package com.xiaozhounandu.mapper;

import com.xiaozhounandu.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMapper {
    User findByUsername(@Param("username") String username);

    User findById(@Param("id") Long id);

    List<User> selectAll();

    int insertUser(User user);

    int updateUser(User user);

    int updatePassword(@Param("id") Long id, @Param("password") String password);

    int updateLastLoginTime(@Param("id") Long id);

    int deleteUserById(@Param("id") Long id);
}