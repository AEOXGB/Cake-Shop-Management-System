package com.fr.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fr.javaBean.Rider;
import com.fr.mapper.RiderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RiderServiceImpl implements RiderService {

    @Autowired
    private RiderMapper riderMapper;

    @Override
    public Rider login(String phone, String password) {
        System.out.println("尝试骑手登录: phone=" + phone + ", password=" + password);
        
        QueryWrapper<Rider> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        Rider rider = riderMapper.selectOne(queryWrapper);
        
        if (rider != null) {
            System.out.println("找到骑手: id=" + rider.getId() + ", name=" + rider.getName() + ", status=" + rider.getStatus());
            if (password.equals(rider.getPassword()) && rider.getStatus() == 1) {
                System.out.println("骑手登录成功");
                return rider;
            } else {
                System.out.println("密码不匹配或状态未审核: passwordMatch=" + password.equals(rider.getPassword()) + ", status=" + rider.getStatus());
            }
        } else {
            System.out.println("未找到骑手");
        }
        return null;
    }

    @Override
    public Rider getRiderById(int id) {
        return riderMapper.selectById(id);
    }

    @Override
    public boolean addRider(Rider rider) {
        return riderMapper.insert(rider) > 0;
    }

    @Override
    public boolean updateRider(Rider rider) {
        return riderMapper.updateById(rider) > 0;
    }

    @Override
    public boolean deleteRider(int id) {
        return riderMapper.deleteById(id) > 0;
    }

    @Override
    public List<Rider> getAllRiders() {
        return riderMapper.selectList(null);
    }

    @Override
    public Rider getRiderByPhone(String phone) {
        QueryWrapper<Rider> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        return riderMapper.selectOne(queryWrapper);
    }
}