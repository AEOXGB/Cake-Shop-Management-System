package com.fr.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fr.javaBean.OrderItem;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {

}
