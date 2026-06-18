package com.fr.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fr.javaBean.Goods;

import java.util.List;

public interface GoodsService {

    public List<Goods> findGoods(QueryWrapper<Goods> goodsQueryWrapper);
    
    public boolean updateGoods(Goods goods, QueryWrapper<Goods> queryWrapper);
    
    public List<Goods> findTopSellingGoods(int limit);
    
    public List<Goods> findNewArrivals(int limit);
    
    public long countGoods();
}
