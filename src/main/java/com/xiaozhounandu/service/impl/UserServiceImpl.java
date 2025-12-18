package com.xiaozhounandu.service.impl;

import com.xiaozhounandu.entity.User;
import com.xiaozhounandu.mapper.UserMapper;
import com.xiaozhounandu.service.UserService;
import com.xiaozhounandu.util.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public User findById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.selectAll();
    }

    @Override
    public int createUser(User user) {
        return userMapper.insertUser(user);
    }

    @Override
    public int updateUser(User user) {
        return userMapper.updateUser(user);
    }

    @Override
    public int updatePassword(Long id, String password) {
        return userMapper.updatePassword(id, password);
    }

    @Override
    public int updateUserStatus(Long id, Integer status) {
        User user = userMapper.findById(id);
        if (user == null) {
            return 0;
        }
        user.setStatus(status);
        return userMapper.updateUser(user);
    }

    @Override
    public int register(User user) {
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
}
