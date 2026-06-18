package com.fr.javaBean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("rider")
public class Rider {

    @TableId(type = IdType.AUTO)
    private int id;
    
    @TableField("phone")
    private String phone;
    
    @TableField("password")
    private String password;
    
    @TableField("name")
    private String name;
    
    @TableField("avatar")
    private String avatar;
    
    @TableField("id_card")
    private String idCard;
    
    @TableField("status")
    private int status;
    
    @TableField("create_time")
    private String createTime;
    
    @TableField("update_time")
    private String updateTime;

    public Rider() {
    }

    public Rider(int id, String phone, String password, String name, String avatar, String idCard, int status, String createTime, String updateTime) {
        this.id = id;
        this.phone = phone;
        this.password = password;
        this.name = name;
        this.avatar = avatar;
        this.idCard = idCard;
        this.status = status;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
}