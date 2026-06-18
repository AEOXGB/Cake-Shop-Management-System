package com.fr.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fr.javaBean.User;

import java.util.List;

public interface UserService {

    public List<User> findUsers(QueryWrapper<User> queryWrapper);
    
    public Page<User> findUsersByPage(Page<User> page, QueryWrapper<User> queryWrapper);

    public int addUser(User user);
    
    public int updateUser(User user);
}
