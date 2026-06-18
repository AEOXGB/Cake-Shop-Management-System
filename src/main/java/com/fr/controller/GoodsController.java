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

    @RequestMapping("/goodsList")
    public ModelAndView goodsList(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        String typeid = request.getParameter("typeid");
        String name = request.getParameter("name");
        String id = request.getParameter("id");

        QueryWrapper<Goods> goodsQueryWrapper = new QueryWrapper<>();

        if (typeid!=null&&!"".equals(typeid.trim())){
            goodsQueryWrapper.eq("type_id",typeid);
        }
        if (name!=null&&!"".equals(name.trim())){
            goodsQueryWrapper.like("name",name);
        }

        if (id!=null&&!"".equals(id.trim())){
            goodsQueryWrapper.eq("id",Integer.parseInt(id));
        }

        List<Goods> goodsList = goodsService.findGoods(goodsQueryWrapper);
        List<Type> types = typeService.findTypes(null);

        modelAndView.addObject("goodsList",goodsList);
        modelAndView.addObject("types",types);

        String msg = (String) request.getSession().getAttribute("msg");
        if (msg != null) {
            modelAndView.addObject("msg", msg);
            request.getSession().removeAttribute("msg");
        }

        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            QueryWrapper<Cart> cartQueryWrapper = new QueryWrapper<>();
            cartQueryWrapper.eq("user_name", user.getUsername());
            long count = cartService.countCart(cartQueryWrapper);
            modelAndView.addObject("count", count);
        }

        if (id!=null&&!"".equals(id.trim())){
            modelAndView.setViewName("goodsDetail");
        } else {
            modelAndView.setViewName("index");
        }

        return modelAndView;
    }
    
    @RequestMapping("/topSell")
    public ModelAndView topSell(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        
        // 查询销售量最高的前5个商品
        List<Goods> topGoodsList = goodsService.findTopSellingGoods(5);
        
        modelAndView.addObject("topGoodsList", topGoodsList);
        modelAndView.setViewName("topSell");
        
        // 获取购物车数量
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            QueryWrapper<Cart> cartQueryWrapper = new QueryWrapper<>();
            cartQueryWrapper.eq("user_name", user.getUsername());
            long count = cartService.countCart(cartQueryWrapper);
            modelAndView.addObject("count", count);
        }
        
        return modelAndView;
    }
    
    @RequestMapping("/newArrivals")
    public ModelAndView newArrivals(HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView();
        
        // 查询最新上架的前5个商品
        List<Goods> newArrivalsList = goodsService.findNewArrivals(5);
        
        modelAndView.addObject("newArrivalsList", newArrivalsList);
        modelAndView.setViewName("newArrivals");
        
        // 获取购物车数量
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
