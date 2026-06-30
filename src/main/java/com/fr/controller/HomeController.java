package com.fr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 首页控制器
 * 所属模块：首页模块
 * 处理网站首页、登录页、注册页等基础页面的跳转请求
 */
@Controller
public class HomeController {

    /**
     * 网站首页方法
     * 访问根路径时，转发到商品列表页面
     * @return String 转发到商品列表的路径
     */
    @RequestMapping("/")
    public String index() {
        return "forward:/goods/goodsList";
    }

    /**
     * 登录页面方法
     * 跳转到登录页面
     * @return String 登录页面视图名
     */
    @RequestMapping("/login.html")
    public String login() {
        return "login";
    }

    /**
     * 注册页面方法
     * 跳转到注册页面
     * @return String 注册页面视图名
     */
    @RequestMapping("/register.html")
    public String register() {
        return "register";
    }
}