package com.fr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fr.javaBean.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 通知数据访问接口
 * 对应数据库表：notification
 * 
 * 功能描述：负责系统通知消息的增删改查操作，主要用于向骑手推送订单相关的通知消息。
 * 主要业务场景：新订单通知、订单状态变更通知、系统公告、通知已读状态管理、未读数量统计等。
 * 
 * 通知信息包含：通知ID、骑手ID、通知标题、通知内容、通知类型、关联订单ID、是否已读、创建时间等。
 * 通知类型包括：新订单提醒、订单取消通知、系统公告等。
 * 
 * 继承自 MyBatis-Plus 的 BaseMapper<Notification>，自动提供以下通用 CRUD 方法：
 * - insert(Notification entity)：新增一条通知消息
 * - deleteById(Serializable id)：根据主键ID删除通知
 * - delete(Wrapper<Notification> wrapper)：根据条件删除通知
 * - updateById(Notification entity)：根据主键ID更新通知信息（如标记已读）
 * - update(Notification entity, Wrapper<Notification> wrapper)：根据条件更新通知信息
 * - selectById(Serializable id)：根据通知ID查询通知详情
 * - selectBatchIds(Collection<? extends Serializable> idList)：根据通知ID批量查询通知
 * - selectOne(Wrapper<Notification> wrapper)：根据条件查询单条通知记录
 * - selectCount(Wrapper<Notification> wrapper)：根据条件统计通知数量
 * - selectList(Wrapper<Notification> wrapper)：根据条件查询通知列表
 * - selectPage(IPage<Notification> page, Wrapper<Notification> wrapper)：分页查询通知列表
 * 
 * @author CakeShop Team
 * @since 1.0.0
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {

    /**
     * 根据骑手ID查询该骑手的所有通知列表
     * 按创建时间倒序排列，最新的通知排在最前面
     * 
     * @param riderId 骑手ID，用于筛选该骑手收到的所有通知
     * @return List<Notification> 通知列表，按创建时间降序排列
     */
    @Select("SELECT * FROM notification WHERE rider_id = #{riderId} ORDER BY create_time DESC")
    List<Notification> selectByRiderId(Integer riderId);

    /**
     * 根据骑手ID和通知类型查询指定类型的通知列表
     * 按创建时间倒序排列，用于筛选特定类型的通知（如只看新订单通知）
     * 
     * @param riderId 骑手ID，用于筛选该骑手收到的通知
     * @param type 通知类型，如"new_order"（新订单）、"system"（系统通知）等
     * @return List<Notification> 指定类型的通知列表，按创建时间降序排列
     */
    @Select("SELECT * FROM notification WHERE rider_id = #{riderId} AND type = #{type} ORDER BY create_time DESC")
    List<Notification> selectByRiderIdAndType(Integer riderId, String type);

    /**
     * 统计指定骑手的未读通知数量
     * 用于在消息图标上显示未读消息角标
     * 
     * @param riderId 骑手ID，用于统计该骑手的未读通知
     * @return Integer 未读通知的数量，is_read = 0 表示未读
     */
    @Select("SELECT COUNT(*) FROM notification WHERE rider_id = #{riderId} AND is_read = 0")
    Integer countUnread(Integer riderId);
}