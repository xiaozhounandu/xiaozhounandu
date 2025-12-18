package com.xiaozhounandu.service;

import com.xiaozhounandu.entity.User;

public interface AuthService {
    User login(String username, String password);

    int register(User user);

    int logout(String token);

    User getCurrentUser(String token);

    int updatePassword(Long userId, String oldPassword, String newPassword);
}
