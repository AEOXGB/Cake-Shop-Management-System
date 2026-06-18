package com.fr.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fr.javaBean.Order;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

}
