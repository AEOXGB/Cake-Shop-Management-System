package com.fr.javaBean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("notification")
public class Notification {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("rider_id")
    private Integer riderId;

    private String title;

    private String content;

    private String type;

    @TableField("order_id")
    private Long orderId;

    @TableField("is_read")
    private Integer isRead;

    @TableField("create_time")
    private String createTime;

    @TableField(exist = false)
    private String time;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getRiderId() { return riderId; }
    public void setRiderId(Integer riderId) { this.riderId = riderId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Integer getIsRead() { return isRead; }
    public void setIsRead(Integer isRead) { this.isRead = isRead; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
}