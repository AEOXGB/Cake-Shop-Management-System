package com.fr.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fr.javaBean.User;
import com.fr.mapper.UserMapper;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    @Resource
    private UserMapper userMapper;


    @Override
    public List<User> findUsers(QueryWrapper<User> queryWrapper) {
        return userMapper.selectList(queryWrapper);
    }

    @Override
    public int addUser(User user) {
        return userMapper.insert(user);
    }

    @Override
    public int updateUser(User user) {
        return userMapper.updateById(user);
    }

    @Override
    public Page<User> findUsersByPage(Page<User> page, QueryWrapper<User> queryWrapper) {
        return userMapper.selectPage(page, queryWrapper);
    }
}
