package com.fr.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fr.javaBean.Rider;
import com.fr.mapper.RiderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 骑手服务实现类
 * 实现 RiderService 接口，提供骑手相关的业务逻辑处理
 * 包括骑手登录、查询、新增、更新、删除等功能
 */
@Service
public class RiderServiceImpl implements RiderService {

    @Autowired
    private RiderMapper riderMapper;

    /**
     * 骑手登录验证
     * 根据手机号和密码进行登录验证，同时验证骑手状态是否为已审核
     * 
     * @param phone 骑手手机号码
     * @param password 骑手密码
     * @return 登录成功返回骑手对象，失败返回null
     */
    @Override
    public Rider login(String phone, String password) {
        System.out.println("尝试骑手登录: phone=" + phone + ", password=" + password);
        
        // 根据手机号查询骑手信息
        QueryWrapper<Rider> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        Rider rider = riderMapper.selectOne(queryWrapper);
        
        if (rider != null) {
            System.out.println("找到骑手: id=" + rider.getId() + ", name=" + rider.getName() + ", status=" + rider.getStatus());
            // 验证密码是否匹配，并且骑手状态为已审核（status=1）
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

    /**
     * 根据ID查询骑手信息
     * 
     * @param id 骑手ID
     * @return 骑手对象，不存在则返回null
     */
    @Override
    public Rider getRiderById(int id) {
        return riderMapper.selectById(id);
    }

    /**
     * 新增骑手
     * 
     * @param rider 待新增的骑手对象
     * @return 新增成功返回true，失败返回false
     */
    @Override
    public boolean addRider(Rider rider) {
        return riderMapper.insert(rider) > 0;
    }

    /**
     * 更新骑手信息
     * 根据骑手ID更新骑手的相关信息
     * 
     * @param rider 待更新的骑手对象，必须包含骑手ID
     * @return 更新成功返回true，失败返回false
     */
    @Override
    public boolean updateRider(Rider rider) {
        return riderMapper.updateById(rider) > 0;
    }

    /**
     * 根据ID删除骑手
     * 
     * @param id 待删除的骑手ID
     * @return 删除成功返回true，失败返回false
     */
    @Override
    public boolean deleteRider(int id) {
        return riderMapper.deleteById(id) > 0;
    }

    /**
     * 查询所有骑手列表
     * 
     * @return 所有骑手的列表
     */
    @Override
    public List<Rider> getAllRiders() {
        return riderMapper.selectList(null);
    }

    /**
     * 根据手机号查询骑手信息
     * 
     * @param phone 骑手手机号码
     * @return 骑手对象，不存在则返回null
     */
    @Override
    public Rider getRiderByPhone(String phone) {
        // 根据手机号构建查询条件
        QueryWrapper<Rider> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        // 查询并返回骑手信息
        return riderMapper.selectOne(queryWrapper);
    }
}
