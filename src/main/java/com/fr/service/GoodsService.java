package com.fr.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fr.javaBean.Goods;

import java.util.List;

/**
 * 商品服务接口
 * 
 * 该接口定义了商品管理相关的业务操作方法，属于商品模块。
 * 提供商品信息的查询、更新、热销商品查询、新品查询和商品统计等核心功能。
 * 
 * @author CakeShop Team
 * @since 1.0
 */
public interface GoodsService {

    /**
     * 根据条件查询商品列表
     * 
     * 根据传入的查询条件构造器，查询符合条件的所有商品信息。
     * 
     * @param goodsQueryWrapper 商品查询条件构造器，用于封装查询条件（如分类、价格区间、状态等）
     * @return 符合条件的商品列表，若没有数据则返回空列表
     */
    public List<Goods> findGoods(QueryWrapper<Goods> goodsQueryWrapper);
    
    /**
     * 更新商品信息
     * 
     * 根据商品对象和查询条件更新商品信息。
     * 
     * @param goods 待更新的商品对象，包含要更新的商品属性
     * @param queryWrapper 查询条件构造器，用于指定更新的条件
     * @return 更新成功返回true，失败返回false
     */
    public boolean updateGoods(Goods goods, QueryWrapper<Goods> queryWrapper);
    
    /**
     * 查询热销商品列表
     * 
     * 查询销量最高的商品列表，按销量降序排列。
     * 
     * @param limit 返回的商品数量上限
     * @return 热销商品列表，按销量从高到低排序
     */
    public List<Goods> findTopSellingGoods(int limit);
    
    /**
     * 查询新品上架商品列表
     * 
     * 查询最新上架的商品列表，按上架时间降序排列。
     * 
     * @param limit 返回的商品数量上限
     * @return 新品商品列表，按上架时间从新到旧排序
     */
    public List<Goods> findNewArrivals(int limit);
    
    /**
     * 统计商品总数
     * 
     * 统计数据库中所有商品的总数量。
     * 
     * @return 商品总数量
     */
    public long countGoods();
}
