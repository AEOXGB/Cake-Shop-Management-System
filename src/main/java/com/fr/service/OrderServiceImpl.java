package com.fr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fr.javaBean.Order;
import com.fr.javaBean.OrderItem;
import com.fr.mapper.OrderItemMapper;
import com.fr.mapper.OrderMapper;

@Service
public class OrderServiceImpl implements OrderService {
	
	@Autowired
	OrderMapper orderMapper;
	
	@Autowired
	OrderItemMapper orderItemMapper;

	@Override
	public int insertOrder(Order order) {
		return orderMapper.insert(order);
	}

	@Override
	public int insertOrderItem(OrderItem orderItem) {
		return orderItemMapper.insert(orderItem);
	}

}
