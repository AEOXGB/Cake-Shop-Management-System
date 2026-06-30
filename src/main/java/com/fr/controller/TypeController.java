package com.fr.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fr.javaBean.Goods;
import com.fr.javaBean.Type;
import com.fr.javaBean.User;
import com.fr.mapper.GoodsMapper;
import com.fr.mapper.TypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 分类控制器
 * 所属模块：商品分类管理模块
 * 处理商品分类相关的请求，包括添加分类、编辑分类、删除分类等功能
 * 请求路径前缀：/admin
 */
@Controller
@RequestMapping("/admin")
public class TypeController {

    @Autowired
    private TypeMapper typeMapper;
    
    @Autowired
    private GoodsMapper goodsMapper;

    /**
     * 添加分类页面方法
     * 跳转到添加分类的页面，需要管理员权限
     * @param request HttpServletRequest请求对象
     * @return ModelAndView 返回添加分类页面
     */
    @RequestMapping("/addType")
    public ModelAndView addType(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || !"1".equals(user.getIsadmin())) {
            modelAndView.setViewName("redirect:/login.jsp");
            return modelAndView;
        }
        
        modelAndView.addObject("active", 1);
        modelAndView.addObject("pageTitle", "添加分类");
        modelAndView.addObject("content", "admin/adminAddType :: content");
        modelAndView.setViewName("admin/adminLayout");
        return modelAndView;
    }

    /**
     * 保存分类方法
     * 新增商品分类，保存分类名称
     * @param request HttpServletRequest请求对象，包含name（分类名称）参数
     * @return String 重定向到分类列表页面
     */
    @RequestMapping("/saveType")
    public String saveType(HttpServletRequest request) {
        String name = request.getParameter("name");
        
        if (name != null && !name.trim().isEmpty()) {
            Type type = new Type();
            type.setName(name.trim());
            typeMapper.insert(type);
        }
        
        return "redirect:/admin/categoryList";
    }

    /**
     * 编辑分类页面方法
     * 跳转到编辑分类的页面，回显分类信息，需要管理员权限
     * @param request HttpServletRequest请求对象，包含id（分类ID）参数
     * @return ModelAndView 返回编辑分类页面
     */
    @RequestMapping("/editType")
    public ModelAndView editType(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || !"1".equals(user.getIsadmin())) {
            modelAndView.setViewName("redirect:/login.jsp");
            return modelAndView;
        }
        
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            int typeId = Integer.parseInt(idStr);
            Type type = typeMapper.selectById(typeId);
            modelAndView.addObject("type", type);
        }
        
        modelAndView.addObject("active", 1);
        modelAndView.addObject("pageTitle", "编辑分类");
        modelAndView.addObject("content", "admin/adminAddType :: content");
        modelAndView.setViewName("admin/adminLayout");
        return modelAndView;
    }

    /**
     * 更新分类方法
     * 更新商品分类的名称
     * @param request HttpServletRequest请求对象，包含id（分类ID）、name（分类名称）参数
     * @return String 重定向到分类列表页面
     */
    @RequestMapping("/updateType")
    public String updateType(HttpServletRequest request) {
        String idStr = request.getParameter("id");
        String name = request.getParameter("name");
        
        if (idStr != null && !idStr.isEmpty() && name != null && !name.trim().isEmpty()) {
            int typeId = Integer.parseInt(idStr);
            Type type = typeMapper.selectById(typeId);
            if (type != null) {
                type.setName(name.trim());
                typeMapper.updateById(type);
            }
        }
        
        return "redirect:/admin/categoryList";
    }

    /**
     * 删除分类方法
     * 删除商品分类，只有当该分类下没有商品时才能删除
     * @param request HttpServletRequest请求对象，包含id（分类ID）参数
     * @return String 重定向到分类列表页面
     */
    @RequestMapping("/deleteType")
    public String deleteType(HttpServletRequest request) {
        String idStr = request.getParameter("id");
        
        if (idStr != null && !idStr.isEmpty()) {
            int typeId = Integer.parseInt(idStr);
            
            // 检查是否有商品使用该分类
            QueryWrapper<Goods> goodsQuery = new QueryWrapper<>();
            goodsQuery.eq("type_id", typeId);
            long count = goodsMapper.selectCount(goodsQuery);
            
            // 如果没有商品使用该分类，才能删除
            if (count == 0) {
                typeMapper.deleteById(typeId);
            }
        }
        
        return "redirect:/admin/categoryList";
    }
}
