package com.fr.javaBean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("cart")
public class Cart {

    @TableId(type = IdType.AUTO)
    int id;
    
    @TableField("good_id")
    int goodId;
    
    @TableField("intro")
    String goodsName;
    
    @TableField("cover")
    String goodsCover;
    
    @TableField("price")
    String goodsPrice;
    
    @TableField("amount")
    int count;
    
    @TableField("total_price")
    double total_price;
    
    @TableField("user_name")
    String userName;

}