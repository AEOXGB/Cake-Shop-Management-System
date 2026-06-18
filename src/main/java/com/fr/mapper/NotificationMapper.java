package com.fr.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fr.javaBean.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {

    @Select("SELECT * FROM notification WHERE rider_id = #{riderId} ORDER BY create_time DESC")
    List<Notification> selectByRiderId(Integer riderId);

    @Select("SELECT * FROM notification WHERE rider_id = #{riderId} AND type = #{type} ORDER BY create_time DESC")
    List<Notification> selectByRiderIdAndType(Integer riderId, String type);

    @Select("SELECT COUNT(*) FROM notification WHERE rider_id = #{riderId} AND is_read = 0")
    Integer countUnread(Integer riderId);
}