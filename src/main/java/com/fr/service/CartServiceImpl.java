package com.fr.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fr.javaBean.Cart;
import com.fr.mapper.CartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 购物车服务实现类
 * 实现 CartService 接口，提供购物车相关的业务逻辑处理
 * 包括购物车查询、新增、更新、删除以及数量统计等功能
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartMapper cartMapper;

    /**
     * 根据查询条件查找购物车记录
     * 
     * @param queryWrapper 查询条件包装器，用于构建SQL查询条件
     * @return 符合条件的购物车记录列表
     */
    @Override
    public List<Cart> findCart(QueryWrapper<Cart> queryWrapper) {
        return cartMapper.selectList(queryWrapper);
    }

    /**
     * 新增购物车记录
     * 
     * @param cart 待新增的购物车对象
     * @return 新增成功返回true，失败返回false
     */
    @Override
    public boolean addCart(Cart cart) {
        return cartMapper.insert(cart) > 0;
    }

    /**
     * 更新购物车记录
     * 根据购物车ID更新购物车的相关信息
     * 
     * @param cart 待更新的购物车对象，必须包含购物车ID
     * @return 更新成功返回true，失败返回false
     */
    @Override
    public boolean updateCart(Cart cart) {
        return cartMapper.updateById(cart) > 0;
    }

    /**
     * 根据ID删除购物车记录
     * 
     * @param id 待删除的购物车记录ID
     * @return 受影响的行数，成功返回1，失败返回0
     */
    @Override
    public int deleteCart(int id) {
        return cartMapper.deleteById(id);
    }

    /**
     * 统计符合条件的购物车记录数量
     * 
     * @param queryWrapper 查询条件包装器，用于构建SQL查询条件
     * @return 符合条件的购物车记录总数
     */
    @Override
    public long countCart(QueryWrapper<Cart> queryWrapper) {
        return cartMapper.selectCount(queryWrapper);
    }

    /**
     * 根据用户名删除该用户的所有购物车记录
     * 通常用于用户下单后清空购物车
     * 
     * @param userName 用户名
     * @return 受影响的行数，即删除的购物车记录数量
     */
    @Override
    public int deleteCartByUserName(String userName) {
        // 构建查询条件：根据用户名匹配购物车记录
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", userName);
        // 删除该用户的所有购物车记录
        return cartMapper.delete(queryWrapper);
    }
}
