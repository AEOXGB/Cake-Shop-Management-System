package com.fr.javaBean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("orderitem")
public class OrderItem {
	private int id;
	private double price;
	private int amount;
	@TableField("goods_id")
	private int goodsId;
	@TableField("order_id")
	private long orderId;
}
