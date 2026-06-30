package com.fr.service;

import com.fr.javaBean.Order;
import com.fr.javaBean.OrderItem;

/**
 * 订单服务接口
 * 
 * 该接口定义了订单管理相关的业务操作方法，属于订单模块。
 * 提供订单创建和订单项添加等核心功能。
 * 
 * @author CakeShop Team
 * @since 1.0
 */
public interface OrderService {
	
	/**
	 * 新增订单
	 * 
	 * 创建一个新的订单记录并保存到数据库中。
	 * 
	 * @param order 订单对象，包含订单编号、用户信息、收货地址、总金额等订单信息
	 * @return 受影响的行数，成功返回1，失败返回0
	 */
	int insertOrder(Order order);
	
	/**
	 * 新增订单项
	 * 
	 * 向订单中添加一个商品明细项，记录该订单包含的商品信息。
	 * 
	 * @param orderItem 订单项对象，包含订单ID、商品ID、商品数量、单价等信息
	 * @return 受影响的行数，成功返回1，失败返回0
	 */
	int insertOrderItem(OrderItem orderItem);

}
