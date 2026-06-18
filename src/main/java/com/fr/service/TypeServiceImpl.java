package com.fr.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fr.javaBean.Type;
import com.fr.mapper.TypeMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class TypeServiceImpl implements TypeService{

    @Resource
    TypeMapper typeMapper;

    @Override
    public List<Type> findTypes(QueryWrapper<Type> typeQueryWrapper) {
        return typeMapper.selectList(typeQueryWrapper);
    }
    
    @Override
    public long countTypes() {
        return typeMapper.selectCount(null);
    }
}
