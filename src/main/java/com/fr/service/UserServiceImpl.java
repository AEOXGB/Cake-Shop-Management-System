package com.fr.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fr.javaBean.User;
import com.fr.mapper.UserMapper;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.util.List;

/**
 * 用户服务实现类
 * 实现 UserService 接口，提供用户相关的业务逻辑处理
 * 包括用户查询、新增、更新以及分页查询等功能
 */
@Service
public class UserServiceImpl implements UserService{

    @Resource
    private UserMapper userMapper;

    /**
     * 根据查询条件查找用户列表
     * 
     * @param queryWrapper 查询条件包装器，用于构建SQL查询条件
     * @return 符合条件的用户列表
     */
    @Override
    public List<User> findUsers(QueryWrapper<User> queryWrapper) {
        return userMapper.selectList(queryWrapper);
    }

    /**
     * 新增用户
     * 
     * @param user 待新增的用户对象
     * @return 受影响的行数，成功返回1，失败返回0
     */
    @Override
    public int addUser(User user) {
        return userMapper.insert(user);
    }

    /**
     * 更新用户信息
     * 根据用户ID更新用户的相关信息
     * 
     * @param user 待更新的用户对象，必须包含用户ID
     * @return 受影响的行数，成功返回1，失败返回0
     */
    @Override
    public int updateUser(User user) {
        return userMapper.updateById(user);
    }

    /**
     * 分页查询用户列表
     * 
     * @param page 分页对象，包含当前页码、每页大小等分页信息
     * @param queryWrapper 查询条件包装器，用于构建SQL查询条件
     * @return 包含用户列表的分页对象
     */
    @Override
    public Page<User> findUsersByPage(Page<User> page, QueryWrapper<User> queryWrapper) {
        return userMapper.selectPage(page, queryWrapper);
    }
}
