package com.fr.javaBean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("goods")
public class Goods {

    @TableId("id")
    int id;
    String name;
    String cover;
    String image1;
    String image2;
    String price;
    String intro;
    int stock;
    @TableField("type_id")
    int typeId;
    String daytime;
    
    @TableField(exist = false)
    String typeName;
    
    @TableField(exist = false)
    String categoryName;
    
    public int getInventory() {
        return stock;
    }
    
    public void setInventory(int inventory) {
        this.stock = inventory;
    }
    
    public int getTypeId() {
        return typeId;
    }
    
    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

}
