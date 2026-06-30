package com.fr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fr.javaBean.Goods;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品数据访问接口
 * 对应数据库表：goods
 * 
 * 功能描述：负责蛋糕商品信息的增删改查操作，包括商品的上架、下架、价格调整、库存管理等。
 * 主要业务场景：商品浏览、商品搜索、商品详情查看、后台商品管理、库存预警等。
 * 
 * 商品信息包含：商品名称、封面图片、详情图片、价格、介绍、库存、分类ID、上架时间等。
 * 
 * 继承自 MyBatis-Plus 的 BaseMapper<Goods>，自动提供以下通用 CRUD 方法：
 * - insert(Goods entity)：插入一条商品记录
 * - deleteById(Serializable id)：根据主键ID删除商品
 * - delete(Wrapper<Goods> wrapper)：根据条件删除商品
 * - updateById(Goods entity)：根据主键ID更新商品信息
 * - update(Goods entity, Wrapper<Goods> wrapper)：根据条件更新商品信息
 * - selectById(Serializable id)：根据主键ID查询商品
 * - selectBatchIds(Collection<? extends Serializable> idList)：根据主键ID批量查询商品
 * - selectOne(Wrapper<Goods> wrapper)：根据条件查询单条商品记录
 * - selectCount(Wrapper<Goods> wrapper)：根据条件统计商品数量
 * - selectList(Wrapper<Goods> wrapper)：根据条件查询商品列表
 * - selectPage(IPage<Goods> page, Wrapper<Goods> wrapper)：分页查询商品列表
 * 
 * @author CakeShop Team
 * @since 1.0.0
 */
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {
}
