package com.fr.javaBean;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("`order`")
public class Order {
	@TableId("id")
	private long id;
	private double total;
	private int amount;
	private int status;
	private int paytype;
	private String name;
	private String phone;
	private String address;
	private String datetime;
	@TableField(value = "user_id", updateStrategy = FieldStrategy.NEVER)
	private int userId;
	@TableField("rider_id")
	private Integer riderId;
	@TableField("pickup_time")
	private String pickupTime;
	@TableField("delivery_time")
	private String deliveryTime;
	@TableField("complete_time")
	private String completeTime;
	private Double commission;
	private String evaluation;
	private Integer rating;
	@TableField(exist = false)
	private List<Map<String, Object>> items;
	@TableField(exist = false)
	private List<Map<String, Object>> goods;
	
	public List<Map<String, Object>> getGoods() {
		return goods;
	}
	
	public void setGoods(List<Map<String, Object>> goods) {
		this.goods = goods;
	}
}
