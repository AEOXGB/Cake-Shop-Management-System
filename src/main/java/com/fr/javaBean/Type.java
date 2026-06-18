package com.fr.javaBean;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("type")
public class Type {

    int id;
    String name;
}
