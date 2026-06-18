package com.fr.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fr.javaBean.Type;

import java.util.List;

public interface TypeService {

    public List<Type> findTypes(QueryWrapper<Type> typeQueryWrapper);
    
    public long countTypes();
}
