package com.xiaozhounandu.service.impl;

import com.xiaozhounandu.entity.User;
import com.xiaozhounandu.mapper.UserMapper;
import com.xiaozhounandu.service.AuthService;
import com.xiaozhounandu.util.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return null;
        }

        // 优先使用BCrypt验证，如果失败则尝试MD5（兼容旧数据）
        boolean passwordValid = false;
        try {
            passwordValid = EncryptionUtil.bcryptMatches(password, user.getPassword());
        } catch (Exception e) {
            // 如果BCrypt验证失败，尝试MD5（仅为兼容旧数据）
            passwordValid = EncryptionUtil.md5(password).equals(user.getPassword());
        }

        if (passwordValid) {
            // 更新最后登录时间
            userMapper.updateLastLoginTime(user.getId());
            return user;
        }
        return null;
    }

    @Override
    public int register(User user) {
        // 检查必要字段
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return -2; // 用户名不能为空
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return -3; // 密码不能为空
        }

        // 检查用户名是否存在
        if (userMapper.findByUsername(user.getUsername()) != null) {
            return -1; // 用户名已存在
        }

        // 使用BCrypt加密密码
        user.setPassword(EncryptionUtil.bcryptEncode(user.getPassword()));
        user.setStatus(1); // 默认启用
        if (user.getRole() == null) {
            user.setRole("USER"); // 默认普通用户
        }

        return userMapper.insertUser(user);
    }

    @Override
    public int logout(String token) {
        // 简单实现：不需要特殊处理，token在客户端删除即可
        return 1;
    }

    @Override
    public User getCurrentUser(String token) {
        // 这个方法在Controller中通过session获取，这里保留用于扩展
        return null;
    }

    @Override
    public int updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.findById(userId);
        if (user == null) {
            return 0;
        }

        // 验证旧密码
        boolean valid = false;
        try {
            valid = EncryptionUtil.bcryptMatches(oldPassword, user.getPassword());
        } catch (Exception e){
            valid = EncryptionUtil.md5(oldPassword).equals(user.getPassword());
        }

        if (!valid) {
            return -1; // 旧密码错误
        }

        // 更新密码（使用BCrypt）
        String encodedNewPassword = EncryptionUtil.bcryptEncode(newPassword);
        return userMapper.updatePassword(userId, encodedNewPassword);
    }
}
