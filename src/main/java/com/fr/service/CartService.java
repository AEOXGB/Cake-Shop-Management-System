package com.fr.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fr.javaBean.Cart;

import java.util.List;

public interface CartService {

    List<Cart> findCart(QueryWrapper<Cart> queryWrapper);

    boolean addCart(Cart cart);

    boolean updateCart(Cart cart);

    int deleteCart(int id);

    long countCart(QueryWrapper<Cart> queryWrapper);
    
    int deleteCartByUserName(String userName);
}