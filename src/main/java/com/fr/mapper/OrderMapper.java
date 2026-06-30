package com.fr.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fr.javaBean.Order;

/**
 * 订单数据访问接口
 * 对应数据库表：`order`
 * 
 * 功能描述：负责订单信息的增删改查操作，包括订单创建、状态流转、支付记录、配送信息管理等。
 * 主要业务场景：用户下单、订单支付、商家接单、骑手配送、订单完成、订单评价、订单查询等。
 * 
 * 订单信息包含：订单ID、订单总金额、商品总数、订单状态、支付方式、收货人姓名、联系电话、
 * 收货地址、下单时间、用户ID、骑手ID、取餐时间、送达时间、完成时间、配送佣金、评价内容、评分等。
 * 
 * 订单状态说明：待支付、已支付/待接单、配送中、已完成、已取消等。
 * 
 * 继承自 MyBatis-Plus 的 BaseMapper<Order>，自动提供以下通用 CRUD 方法：
 * - insert(Order entity)：创建一条新订单
 * - deleteById(Serializable id)：根据主键ID删除订单
 * - delete(Wrapper<Order> wrapper)：根据条件删除订单
 * - updateById(Order entity)：根据主键ID更新订单信息（如更新订单状态、配送信息等）
 * - update(Order entity, Wrapper<Order> wrapper)：根据条件更新订单信息
 * - selectById(Serializable id)：根据订单ID查询订单详情
 * - selectBatchIds(Collection<? extends Serializable> idList)：根据订单ID批量查询订单
 * - selectOne(Wrapper<Order> wrapper)：根据条件查询单条订单记录
 * - selectCount(Wrapper<Order> wrapper)：根据条件统计订单数量
 * - selectList(Wrapper<Order> wrapper)：根据条件查询订单列表（如查询某用户的所有订单）
 * - selectPage(IPage<Order> page, Wrapper<Order> wrapper)：分页查询订单列表
 * 
 * @author CakeShop Team
 * @since 1.0.0
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

}
