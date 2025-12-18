package com.xiaozhounandu.service;

import com.xiaozhounandu.entity.User;

import java.util.List;

public interface UserService {
    User findByUsername(String username);

    User findById(Long id);

    List<User> getAllUsers();

    int createUser(User user);

    int updateUser(User user);

    int updatePassword(Long id, String password);

    int updateUserStatus(Long id, Integer status);

    // 注册用户（带密码加密）
    int register(User user);

    // 统计用户总数
    long countTotalUsers();
}
