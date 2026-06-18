package com.fr.javaBean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private int id;
    private String username;
    @Email(message = "邮箱格式不正确")
    private String email;
    private String password;
    private String name;
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
    private String address;
    private String isadmin;
    private String isvalidate;
    private String createtime;

    public User(){

    }

    public User(int id, String username, String email, String password, String name, String phone, String address, String isadmin, String isvalidate) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.isadmin = isadmin;
        this.isvalidate = isvalidate;
    }
    
    public User(int id, String username, String email, String password, String name, String phone, String address, String isadmin, String isvalidate, String createtime) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.isadmin = isadmin;
        this.isvalidate = isvalidate;
        this.createtime = createtime;
    }
}
