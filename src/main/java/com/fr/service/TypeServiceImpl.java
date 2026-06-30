package com.fr.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fr.javaBean.Type;
import com.fr.mapper.TypeMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 分类服务实现类
 * 实现 TypeService 接口，提供商品分类相关的业务逻辑处理
 * 包括分类查询和分类数量统计等功能
 */
@Service
public class TypeServiceImpl implements TypeService{

    @Resource
    TypeMapper typeMapper;

    /**
     * 根据查询条件查找商品分类列表
     * 
     * @param typeQueryWrapper 分类查询条件包装器，用于构建SQL查询条件
     * @return 符合条件的商品分类列表
     */
    @Override
    public List<Type> findTypes(QueryWrapper<Type> typeQueryWrapper) {
        return typeMapper.selectList(typeQueryWrapper);
    }
    
    /**
     * 统计商品分类总数
     * 
     * @return 商品分类总数量
     */
    @Override
    public long countTypes() {
        return typeMapper.selectCount(null);
    }
}
