package com.fr.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fr.javaBean.Goods;
import com.fr.javaBean.OrderItem;
import com.fr.javaBean.Type;
import com.fr.mapper.GoodsMapper;
import com.fr.mapper.OrderItemMapper;
import com.fr.mapper.TypeMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品服务实现类
 * 实现 GoodsService 接口，提供商品相关的业务逻辑处理
 * 包括商品查询、更新、热销商品统计、新品上架查询以及商品数量统计等功能
 */
@Service
public class GoodsServiceImpl implements GoodsService{
    @Resource
    GoodsMapper goodsMapper;
    
    @Resource
    TypeMapper typeMapper;
    
    @Resource
    OrderItemMapper orderItemMapper;

    /**
     * 根据查询条件查找商品列表
     * 
     * @param goodsQueryWrapper 商品查询条件包装器，用于构建SQL查询条件
     * @return 符合条件的商品列表
     */
    @Override
    public List<Goods> findGoods(QueryWrapper<Goods> goodsQueryWrapper) {
        return goodsMapper.selectList(goodsQueryWrapper);
    }

    /**
     * 更新商品信息
     * 在更新前会验证商品分类ID是否合法
     * 
     * @param goods 待更新的商品对象
     * @param queryWrapper 更新条件包装器
     * @return 更新成功返回true，失败返回false
     * @throws RuntimeException 当商品分类不存在时抛出运行时异常
     */
    @Override
    public boolean updateGoods(Goods goods, QueryWrapper<Goods> queryWrapper) {
        // 如果设置了分类ID，先验证分类是否存在
        if (goods.getTypeId() != 0) {
            // 根据分类ID查询分类信息
            QueryWrapper<Type> typeQueryWrapper = new QueryWrapper<>();
            typeQueryWrapper.eq("id", goods.getTypeId());
            List<Type> typeList = typeMapper.selectList(typeQueryWrapper);
            
            // 分类不存在则抛出异常
            if (typeList == null || typeList.isEmpty()) {
                throw new RuntimeException("请选择正确的商品分类");
            }
        }
        
        // 执行商品更新操作
        return goodsMapper.update(goods, queryWrapper) > 0;
    }
    
    /**
     * 查询热销商品排行榜
     * 根据订单明细统计每个商品的销售数量，按销量降序排列
     * 
     * @param limit 返回的热销商品数量上限
     * @return 按销量降序排列的热销商品列表
     */
    @Override
    public List<Goods> findTopSellingGoods(int limit) {
        // 查询所有订单项
        List<OrderItem> allOrderItems = orderItemMapper.selectList(null);
        
        // 按商品ID分组，统计每个商品的总销售量
        Map<Integer, Integer> salesCountMap = new HashMap<>();
        for (OrderItem item : allOrderItems) {
            int goodsId = item.getGoodsId();
            int amount = item.getAmount();
            // 累加每个商品的销售数量
            salesCountMap.put(goodsId, salesCountMap.getOrDefault(goodsId, 0) + amount);
        }
        
        // 按销售量降序排序，取前limit个商品ID
        List<Integer> topGoodsIds = salesCountMap.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        
        // 如果没有销售记录，返回空列表
        if (topGoodsIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 根据商品ID列表查询对应的商品详细信息
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", topGoodsIds);
        List<Goods> topGoods = goodsMapper.selectList(queryWrapper);
        
        // 按销售量排序，确保返回顺序与销量排名一致
        topGoods.sort((g1, g2) -> {
            int index1 = topGoodsIds.indexOf(g1.getId());
            int index2 = topGoodsIds.indexOf(g2.getId());
            return Integer.compare(index1, index2);
        });
        
        return topGoods;
    }
    
    /**
     * 查询最新上架的商品
     * 按照上架时间降序排列，返回指定数量的新品
     * 
     * @param limit 返回的新品数量上限
     * @return 按上架时间降序排列的新品列表
     */
    @Override
    public List<Goods> findNewArrivals(int limit) {
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        // 按照daytime字段降序排序，获取最新上架的商品
        queryWrapper.orderByDesc("daytime").last("LIMIT " + limit);
        return goodsMapper.selectList(queryWrapper);
    }
    
    /**
     * 统计商品总数
     * 
     * @return 商品总数量
     */
    @Override
    public long countGoods() {
        return goodsMapper.selectCount(null);
    }
}
