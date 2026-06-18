package com.fr.service;

import com.fr.javaBean.Order;
import com.fr.javaBean.OrderItem;
import com.fr.mapper.OrderItemMapper;
import com.fr.mapper.OrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderItemMapper orderItemMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;
    private OrderItem testOrderItem;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1);
        testOrder.setUserId(1);
        testOrder.setTotal(99.9);
        testOrder.setAmount(1);
        testOrder.setStatus(1);
        testOrder.setAddress("test address");

        testOrderItem = new OrderItem();
        testOrderItem.setOrderId(1);
        testOrderItem.setGoodsId(1);
        testOrderItem.setPrice(99.9);
        testOrderItem.setAmount(1);
    }

    @Test
    @DisplayName("testInsertOrder_Success")
    void testInsertOrder_Success() {
        when(orderMapper.insert(any(Order.class))).thenReturn(1);
        int result = orderService.insertOrder(testOrder);
        assertEquals(1, result);
        verify(orderMapper, times(1)).insert(any(Order.class));
    }

    @Test
    @DisplayName("testInsertOrderItem_Success")
    void testInsertOrderItem_Success() {
        when(orderItemMapper.insert(any(OrderItem.class))).thenReturn(1);
        int result = orderService.insertOrderItem(testOrderItem);
        assertEquals(1, result);
        verify(orderItemMapper, times(1)).insert(any(OrderItem.class));
    }
}