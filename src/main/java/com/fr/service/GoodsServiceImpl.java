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

@Service
public class GoodsServiceImpl implements GoodsService{
    @Resource
    GoodsMapper goodsMapper;
    
    @Resource
    TypeMapper typeMapper;
    
    @Resource
    OrderItemMapper orderItemMapper;

    @Override
    public List<Goods> findGoods(QueryWrapper<Goods> goodsQueryWrapper) {
        return goodsMapper.selectList(goodsQueryWrapper);
    }

    @Override
    public boolean updateGoods(Goods goods, QueryWrapper<Goods> queryWrapper) {
        if (goods.getTypeId() != 0) {
            QueryWrapper<Type> typeQueryWrapper = new QueryWrapper<>();
            typeQueryWrapper.eq("id", goods.getTypeId());
            List<Type> typeList = typeMapper.selectList(typeQueryWrapper);
            
            if (typeList == null || typeList.isEmpty()) {
                throw new RuntimeException("请选择正确的商品分类");
            }
        }
        
        return goodsMapper.update(goods, queryWrapper) > 0;
    }
    
    @Override
    public List<Goods> findTopSellingGoods(int limit) {
        // 查询所有订单项
        List<OrderItem> allOrderItems = orderItemMapper.selectList(null);
        
        // 按商品ID分组，统计每个商品的总销售量
        Map<Integer, Integer> salesCountMap = new HashMap<>();
        for (OrderItem item : allOrderItems) {
            int goodsId = item.getGoodsId();
            int amount = item.getAmount();
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
        
        // 查询对应的商品信息
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", topGoodsIds);
        List<Goods> topGoods = goodsMapper.selectList(queryWrapper);
        
        // 按销售量排序，确保返回顺序正确
        topGoods.sort((g1, g2) -> {
            int index1 = topGoodsIds.indexOf(g1.getId());
            int index2 = topGoodsIds.indexOf(g2.getId());
            return Integer.compare(index1, index2);
        });
        
        return topGoods;
    }
    
    @Override
    public List<Goods> findNewArrivals(int limit) {
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        // 按照daytime字段降序排序，获取最新上架的商品
        queryWrapper.orderByDesc("daytime").last("LIMIT " + limit);
        return goodsMapper.selectList(queryWrapper);
    }
    
    @Override
    public long countGoods() {
        return goodsMapper.selectCount(null);
    }
}
