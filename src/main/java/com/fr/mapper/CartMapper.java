package com.fr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fr.javaBean.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 购物车数据访问接口
 * 对应数据库表：cart
 * 
 * 功能描述：负责用户购物车数据的增删改查操作，包括商品添加、数量修改、商品移除、购物车清空等。
 * 主要业务场景：用户浏览商品时加入购物车、购物车页面商品数量调整、结算前购物车商品确认等。
 * 
 * 购物车信息包含：商品ID、商品名称、商品封面图、商品价格、购买数量、小计金额、所属用户名等。
 * 
 * 继承自 MyBatis-Plus 的 BaseMapper<Cart>，自动提供以下通用 CRUD 方法：
 * - insert(Cart entity)：向购物车插入一条商品记录
 * - deleteById(Serializable id)：根据主键ID删除购物车商品
 * - delete(Wrapper<Cart> wrapper)：根据条件删除购物车商品（如清空某用户购物车）
 * - updateById(Cart entity)：根据主键ID更新购物车商品信息（如修改数量）
 * - update(Cart entity, Wrapper<Cart> wrapper)：根据条件更新购物车商品信息
 * - selectById(Serializable id)：根据主键ID查询购物车商品
 * - selectBatchIds(Collection<? extends Serializable> idList)：根据主键ID批量查询购物车商品
 * - selectOne(Wrapper<Cart> wrapper)：根据条件查询单条购物车记录
 * - selectCount(Wrapper<Cart> wrapper)：根据条件统计购物车商品数量
 * - selectList(Wrapper<Cart> wrapper)：根据条件查询购物车列表（如查询某用户的所有购物车商品）
 * - selectPage(IPage<Cart> page, Wrapper<Cart> wrapper)：分页查询购物车列表
 * 
 * @author CakeShop Team
 * @since 1.0.0
 */
@Mapper
@Repository
public interface CartMapper extends BaseMapper<Cart> {
}