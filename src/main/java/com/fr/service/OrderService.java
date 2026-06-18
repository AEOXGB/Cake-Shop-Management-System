package com.fr.service;

import com.fr.javaBean.Order;
import com.fr.javaBean.OrderItem;

public interface OrderService {
	
	int insertOrder(Order order);
	
	int insertOrderItem(OrderItem orderItem);

}
