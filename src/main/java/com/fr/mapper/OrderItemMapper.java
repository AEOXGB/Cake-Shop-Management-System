package com.fr.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fr.javaBean.OrderItem;

/**
 * 订单项数据访问接口
 * 对应数据库表：orderitem
 * 
 * 功能描述：负责订单明细（订单项）的增删改查操作，记录每个订单中包含的具体商品信息。
 * 主要业务场景：订单创建时批量插入订单项、查看订单详情时查询订单项列表、统计商品销量等。
 * 
 * 订单项信息包含：订单项ID、商品单价、购买数量、商品ID、所属订单ID等。
 * 一个订单对应多个订单项，每个订单项对应一个商品及其购买数量。
 * 
 * 继承自 MyBatis-Plus 的 BaseMapper<OrderItem>，自动提供以下通用 CRUD 方法：
 * - insert(OrderItem entity)：插入一条订单项记录
 * - deleteById(Serializable id)：根据主键ID删除订单项
 * - delete(Wrapper<OrderItem> wrapper)：根据条件删除订单项（如删除某订单的所有订单项）
 * - updateById(OrderItem entity)：根据主键ID更新订单项信息
 * - update(OrderItem entity, Wrapper<OrderItem> wrapper)：根据条件更新订单项信息
 * - selectById(Serializable id)：根据主键ID查询订单项
 * - selectBatchIds(Collection<? extends Serializable> idList)：根据主键ID批量查询订单项
 * - selectOne(Wrapper<OrderItem> wrapper)：根据条件查询单条订单项记录
 * - selectCount(Wrapper<OrderItem> wrapper)：根据条件统计订单项数量
 * - selectList(Wrapper<OrderItem> wrapper)：根据条件查询订单项列表（如查询某订单的所有商品明细）
 * - selectPage(IPage<OrderItem> page, Wrapper<OrderItem> wrapper)：分页查询订单项列表
 * 
 * @author CakeShop Team
 * @since 1.0.0
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {

}
