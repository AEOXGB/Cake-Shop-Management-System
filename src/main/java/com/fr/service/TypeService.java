package com.fr.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fr.javaBean.Type;

import java.util.List;

/**
 * 分类服务接口
 * 
 * 该接口定义了商品分类管理相关的业务操作方法，属于商品分类模块。
 * 提供分类查询和分类统计等核心功能。
 * 
 * @author CakeShop Team
 * @since 1.0
 */
public interface TypeService {

    /**
     * 根据条件查询分类列表
     * 
     * 根据传入的查询条件构造器，查询符合条件的商品分类列表。
     * 
     * @param typeQueryWrapper 分类查询条件构造器，用于封装查询条件（如分类名称、状态等）
     * @return 符合条件的分类列表，若没有数据则返回空列表
     */
    public List<Type> findTypes(QueryWrapper<Type> typeQueryWrapper);
    
    /**
     * 统计分类总数
     * 
     * 统计数据库中所有商品分类的总数量。
     * 
     * @return 分类总数量
     */
    public long countTypes();
}
