package com.xiaozhounandu.service;

import com.xiaozhounandu.entity.User;
import com.xiaozhounandu.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public User findById(Long id) {
        return userMapper.findById(id);
    }

    public User login(String username, String password) {
        User user = findByUsername(username);
        if (user != null && user.getPassword().equals(md5(password))) {
            return user;
        }
        return null;
    }

    public int register(User user) {
        // 检查用户名是否已存在
        if (findByUsername(user.getUsername()) != null) {
            return -1; // 用户名已存在
        }
        // 密码加密
        user.setPassword(md5(user.getPassword()));
        user.setStatus(1); // 默认启用
        if (user.getRole() == null) {
            user.setRole("USER"); // 默认普通用户
        }
        return userMapper.insertUser(user);
    }

    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }
}