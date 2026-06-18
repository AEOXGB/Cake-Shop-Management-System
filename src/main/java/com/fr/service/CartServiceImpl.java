package com.fr.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fr.javaBean.Cart;
import com.fr.mapper.CartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartMapper cartMapper;

    @Override
    public List<Cart> findCart(QueryWrapper<Cart> queryWrapper) {
        return cartMapper.selectList(queryWrapper);
    }

    @Override
    public boolean addCart(Cart cart) {
        return cartMapper.insert(cart) > 0;
    }

    @Override
    public boolean updateCart(Cart cart) {
        return cartMapper.updateById(cart) > 0;
    }

    @Override
    public int deleteCart(int id) {
        return cartMapper.deleteById(id);
    }

    @Override
    public long countCart(QueryWrapper<Cart> queryWrapper) {
        return cartMapper.selectCount(queryWrapper);
    }

    @Override
    public int deleteCartByUserName(String userName) {
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", userName);
        return cartMapper.delete(queryWrapper);
    }
}