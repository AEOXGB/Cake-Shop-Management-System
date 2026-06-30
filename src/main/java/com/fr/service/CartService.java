package com.fr.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fr.javaBean.Cart;

import java.util.List;

/**
 * 购物车服务接口
 * 
 * 该接口定义了购物车管理相关的业务操作方法，属于购物车模块。
 * 提供购物车查询、添加商品、更新商品数量、删除商品和购物车统计等核心功能。
 * 
 * @author CakeShop Team
 * @since 1.0
 */
public interface CartService {

    /**
     * 根据条件查询购物车列表
     * 
     * 根据传入的查询条件构造器，查询符合条件的购物车记录。
     * 
     * @param queryWrapper 查询条件构造器，用于封装查询条件（如用户名、商品ID等）
     * @return 符合条件的购物车列表，若没有数据则返回空列表
     */
    List<Cart> findCart(QueryWrapper<Cart> queryWrapper);

    /**
     * 添加商品到购物车
     * 
     * 将商品添加到用户的购物车中。
     * 
     * @param cart 购物车对象，包含用户信息、商品信息和数量等
     * @return 添加成功返回true，失败返回false
     */
    boolean addCart(Cart cart);

    /**
     * 更新购物车信息
     * 
     * 更新购物车中的商品数量或其他信息。
     * 
     * @param cart 待更新的购物车对象，需包含购物车ID及要更新的属性
     * @return 更新成功返回true，失败返回false
     */
    boolean updateCart(Cart cart);

    /**
     * 删除购物车记录
     * 
     * 根据购物车ID删除指定的购物车记录。
     * 
     * @param id 购物车记录ID
     * @return 受影响的行数，成功返回1，失败返回0
     */
    int deleteCart(int id);

    /**
     * 统计购物车记录数量
     * 
     * 根据查询条件统计购物车记录的数量。
     * 
     * @param queryWrapper 查询条件构造器，用于封装统计条件
     * @return 符合条件的购物车记录数量
     */
    long countCart(QueryWrapper<Cart> queryWrapper);
    
    /**
     * 根据用户名删除购物车记录
     * 
     * 删除指定用户的所有购物车记录，通常用于下单后清空购物车。
     * 
     * @param userName 用户名
     * @return 受影响的行数，即删除的购物车记录数量
     */
    int deleteCartByUserName(String userName);
}