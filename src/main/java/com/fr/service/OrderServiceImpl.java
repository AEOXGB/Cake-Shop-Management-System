package com.fr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fr.javaBean.Order;
import com.fr.javaBean.OrderItem;
import com.fr.mapper.OrderItemMapper;
import com.fr.mapper.OrderMapper;

/**
 * 订单服务实现类
 * 实现 OrderService 接口，提供订单相关的业务逻辑处理
 * 包括订单主表的插入和订单明细的插入等功能
 */
@Service
public class OrderServiceImpl implements OrderService {
	
	@Autowired
	OrderMapper orderMapper;
	
	@Autowired
	OrderItemMapper orderItemMapper;

	/**
	 * 插入订单主表记录
	 * 创建新的订单主记录
	 * 
	 * @param order 待插入的订单对象
	 * @return 受影响的行数，成功返回1，失败返回0
	 */
	@Override
	public int insertOrder(Order order) {
		return orderMapper.insert(order);
	}

	/**
	 * 插入订单明细记录
	 * 向订单明细表中添加一条商品明细记录
	 * 
	 * @param orderItem 待插入的订单项对象
	 * @return 受影响的行数，成功返回1，失败返回0
	 */
	@Override
	public int insertOrderItem(OrderItem orderItem) {
		return orderItemMapper.insert(orderItem);
	}

}
