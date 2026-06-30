package com.fr.service;

import com.fr.javaBean.Rider;

import java.util.List;

/**
 * 骑手服务接口
 * 
 * 该接口定义了骑手管理相关的业务操作方法，属于骑手模块。
 * 提供骑手登录、信息查询、新增、更新、删除和列表查询等核心功能。
 * 
 * @author CakeShop Team
 * @since 1.0
 */
public interface RiderService {

    /**
     * 骑手登录
     * 
     * 根据手机号和密码进行骑手身份验证，验证成功返回骑手信息。
     * 
     * @param phone 骑手手机号码
     * @param password 骑手密码
     * @return 登录成功返回骑手对象，失败返回null
     */
    Rider login(String phone, String password);

    /**
     * 根据ID查询骑手信息
     * 
     * 根据骑手ID查询骑手的详细信息。
     * 
     * @param id 骑手ID
     * @return 骑手对象，若不存在则返回null
     */
    Rider getRiderById(int id);

    /**
     * 新增骑手
     * 
     * 将骑手信息保存到数据库中。
     * 
     * @param rider 待新增的骑手对象，包含骑手的各项属性信息
     * @return 新增成功返回true，失败返回false
     */
    boolean addRider(Rider rider);

    /**
     * 更新骑手信息
     * 
     * 根据骑手ID更新骑手的相关信息。
     * 
     * @param rider 待更新的骑手对象，需包含骑手ID及要更新的属性
     * @return 更新成功返回true，失败返回false
     */
    boolean updateRider(Rider rider);

    /**
     * 删除骑手
     * 
     * 根据骑手ID删除指定的骑手记录。
     * 
     * @param id 骑手ID
     * @return 删除成功返回true，失败返回false
     */
    boolean deleteRider(int id);

    /**
     * 查询所有骑手列表
     * 
     * 查询系统中所有的骑手信息。
     * 
     * @return 所有骑手的列表，若没有数据则返回空列表
     */
    List<Rider> getAllRiders();

    /**
     * 根据手机号查询骑手信息
     * 
     * 根据骑手手机号查询骑手的详细信息。
     * 
     * @param phone 骑手手机号码
     * @return 骑手对象，若不存在则返回null
     */
    Rider getRiderByPhone(String phone);
}