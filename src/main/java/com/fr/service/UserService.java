package com.fr.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fr.javaBean.User;

import java.util.List;

/**
 * 用户服务接口
 * 
 * 该接口定义了用户管理相关的业务操作方法，属于用户模块。
 * 提供用户信息的查询、分页查询、新增和更新等核心功能。
 * 
 * @author CakeShop Team
 * @since 1.0
 */
public interface UserService {

    /**
     * 根据条件查询用户列表
     * 
     * 根据传入的查询条件构造器，查询符合条件的所有用户信息。
     * 
     * @param queryWrapper 查询条件构造器，用于封装查询条件（如用户名、状态等）
     * @return 符合条件的用户列表，若没有数据则返回空列表
     */
    public List<User> findUsers(QueryWrapper<User> queryWrapper);
    
    /**
     * 分页查询用户列表
     * 
     * 根据分页对象和查询条件构造器，进行分页查询，返回分页结果。
     * 
     * @param page 分页对象，包含当前页码、每页大小等分页参数
     * @param queryWrapper 查询条件构造器，用于封装查询条件
     * @return 包含用户列表的分页对象，包含总记录数、当前页数据等信息
     */
    public Page<User> findUsersByPage(Page<User> page, QueryWrapper<User> queryWrapper);

    /**
     * 新增用户
     * 
     * 将用户信息保存到数据库中。
     * 
     * @param user 待新增的用户对象，包含用户的各项属性信息
     * @return 受影响的行数，成功返回1，失败返回0
     */
    public int addUser(User user);
    
    /**
     * 更新用户信息
     * 
     * 根据用户ID更新用户的相关信息。
     * 
     * @param user 待更新的用户对象，需包含用户ID及要更新的属性
     * @return 受影响的行数，成功返回1，失败返回0
     */
    public int updateUser(User user);
}
