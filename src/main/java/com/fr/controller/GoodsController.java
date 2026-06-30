package com.fr.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fr.javaBean.Cart;
import com.fr.javaBean.Goods;
import com.fr.javaBean.Type;
import com.fr.javaBean.User;
import com.fr.service.CartService;
import com.fr.service.GoodsService;
import com.fr.service.TypeService;
import com.fr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 商品控制器
 * 所属模块：商品管理模块
 * 处理商品相关的请求，包括商品列表、商品详情、热销商品、新品上市等功能
 * 请求路径前缀：/goods
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    GoodsService goodsService;

    @Autowired
    TypeService typeService;

    @Autowired
    UserService userService;

    @Autowired
    CartService cartService;

    /**
     * 商品列表/详情方法
     * 根据参数查询商品列表或单个商品详情，支持按分类ID和商品名称筛选
     * @param request HttpServletRequest请求对象，包含typeid（分类ID）、name（商品名称）、id（商品ID）等参数
     * @return ModelAndView 有id参数返回商品详情页，否则返回商品列表页（首页）
     */
    @RequestMapping("/goodsList")
    public ModelAndView goodsList(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        // 获取请求参数：分类ID、商品名称、商品ID
        String typeid = request.getParameter("typeid");
        String name = request.getParameter("name");
        String id = request.getParameter("id");

        // 构建商品查询条件
        QueryWrapper<Goods> goodsQueryWrapper = new QueryWrapper<>();

        // 按分类ID筛选
        if (typeid!=null&&!"".equals(typeid.trim())){
            goodsQueryWrapper.eq("type_id",typeid);
        }
        // 按商品名称模糊查询
        if (name!=null&&!"".equals(name.trim())){
            goodsQueryWrapper.like("name",name);
        }

        // 按商品ID精确查询
        if (id!=null&&!"".equals(id.trim())){
            goodsQueryWrapper.eq("id",Integer.parseInt(id));
        }

        // 查询商品列表和所有分类
        List<Goods> goodsList = goodsService.findGoods(goodsQueryWrapper);
        List<Type> types = typeService.findTypes(null);

        // 将商品列表和分类列表存入Model
        modelAndView.addObject("goodsList",goodsList);
        modelAndView.addObject("types",types);

        // 从Session中获取并清除提示消息
        String msg = (String) request.getSession().getAttribute("msg");
        if (msg != null) {
            modelAndView.addObject("msg", msg);
            request.getSession().removeAttribute("msg");
        }

        // 如果用户已登录，查询购物车商品数量
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            QueryWrapper<Cart> cartQueryWrapper = new QueryWrapper<>();
            cartQueryWrapper.eq("user_name", user.getUsername());
            long count = cartService.countCart(cartQueryWrapper);
            modelAndView.addObject("count", count);
        }

        // 根据是否有id参数决定返回详情页还是列表页
        if (id!=null&&!"".equals(id.trim())){
            modelAndView.setViewName("goodsDetail");
        } else {
            modelAndView.setViewName("index");
        }

        return modelAndView;
    }
    
    /**
     * 热销商品方法
     * 查询销售量最高的前5个商品并展示
     * @param request HttpServletRequest请求对象
     * @return ModelAndView 返回热销商品页面
     */
    @RequestMapping("/topSell")
    public ModelAndView topSell(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        
        // 查询销售量最高的前5个商品
        List<Goods> topGoodsList = goodsService.findTopSellingGoods(5);
        
        modelAndView.addObject("topGoodsList", topGoodsList);
        modelAndView.setViewName("topSell");
        
        // 如果用户已登录，获取购物车商品数量
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            QueryWrapper<Cart> cartQueryWrapper = new QueryWrapper<>();
            cartQueryWrapper.eq("user_name", user.getUsername());
            long count = cartService.countCart(cartQueryWrapper);
            modelAndView.addObject("count", count);
        }
        
        return modelAndView;
    }
    
    /**
     * 新品上市方法
     * 查询最新上架的前5个商品并展示
     * @param request HttpServletRequest请求对象
     * @return ModelAndView 返回新品上市页面
     */
    @RequestMapping("/newArrivals")
    public ModelAndView newArrivals(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        
        // 查询最新上架的前5个商品
        List<Goods> newArrivalsList = goodsService.findNewArrivals(5);
        
        modelAndView.addObject("newArrivalsList", newArrivalsList);
        modelAndView.setViewName("newArrivals");
        
        // 如果用户已登录，获取购物车商品数量
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            QueryWrapper<Cart> cartQueryWrapper = new QueryWrapper<>();
            cartQueryWrapper.eq("user_name", user.getUsername());
            long count = cartService.countCart(cartQueryWrapper);
            modelAndView.addObject("count", count);
        }
        
        return modelAndView;
    }
}
