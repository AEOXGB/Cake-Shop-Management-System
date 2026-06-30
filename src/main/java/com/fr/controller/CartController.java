package com.fr.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fr.javaBean.Cart;
import com.fr.javaBean.Goods;
import com.fr.javaBean.Type;
import com.fr.javaBean.User;
import com.fr.service.CartService;
import com.fr.service.GoodsService;
import com.fr.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 购物车控制器
 * 所属模块：购物车模块
 * 处理购物车相关的请求，包括添加商品到购物车、购物车列表、删除购物车商品、修改商品数量等功能
 * 请求路径前缀：/cart
 */
@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    CartService cartService;

    @Autowired
    GoodsService goodsService;
    
    @Autowired
    TypeService typeService;

    /**
     * 添加商品到购物车方法
     * 将指定商品添加到用户购物车中，如果商品已在购物车中则数量加1，同时扣减商品库存
     * @param request HttpServletRequest请求对象，包含id（商品ID）、flag（来源标识，1表示从列表页添加）等参数
     * @return ModelAndView 添加成功后根据来源跳转，失败返回相应页面并提示错误信息
     */
    @RequestMapping("/addToCart")
    public ModelAndView addToCart(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        // 获取商品ID和来源标识
        String goodsId = request.getParameter("id");
        String flag = request.getParameter("flag");

        // 获取当前登录用户
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        // 未登录则跳转到登录页
        if (user == null) {
            modelAndView.addObject("msg", "请先登录！");
            modelAndView.setViewName("login");
            return modelAndView;
        }

        String userName = user.getUsername();

        // 根据商品ID查询商品信息
        QueryWrapper<Goods> goodsQueryWrapper = new QueryWrapper<>();
        goodsQueryWrapper.eq("id", Integer.parseInt(goodsId));
        List<Goods> goodsList = goodsService.findGoods(goodsQueryWrapper);
        
        // 商品不存在，返回首页
        if (goodsList.isEmpty()) {
            modelAndView.addObject("msg", "商品不存在！");
            modelAndView.setViewName("index");
            return modelAndView;
        }

        Goods goods = goodsList.get(0);
        
        // 库存不足，返回提示
        if (goods.getStock() <= 0) {
            modelAndView.addObject("msg", "库存不足请叫管理员加货");
            
            // 查询购物车数量用于页面显示
            QueryWrapper<Cart> countWrapper = new QueryWrapper<>();
            countWrapper.eq("user_name", userName);
            long count = cartService.countCart(countWrapper);
            modelAndView.addObject("count", count);
            
            // 根据来源标识决定返回列表页还是详情页
            if ("1".equals(flag)) {
                modelAndView.addObject("goodsList", goodsService.findGoods(null));
                modelAndView.addObject("types", typeService.findTypes(null));
                modelAndView.setViewName("index");
            } else {
                QueryWrapper<Goods> detailWrapper = new QueryWrapper<>();
                detailWrapper.eq("id", goodsId);
                modelAndView.addObject("goodsList", goodsService.findGoods(detailWrapper));
                modelAndView.addObject("types", typeService.findTypes(null));
                modelAndView.setViewName("goodsDetail");
            }
            return modelAndView;
        }

        // 查询该商品是否已在购物车中
        QueryWrapper<Cart> cartQueryWrapper = new QueryWrapper<>();
        cartQueryWrapper.eq("good_id", goodsId);
        cartQueryWrapper.eq("user_name", userName);
        List<Cart> cartList = cartService.findCart(cartQueryWrapper);

        

        if (cartList.isEmpty()) {
            // 购物车中不存在该商品，新增一条购物车记录
            Cart cart = new Cart();
            cart.setGoodId(goods.getId());
            cart.setGoodsName(goods.getName());
            cart.setGoodsCover(goods.getCover());
            cart.setGoodsPrice(goods.getPrice());
            cart.setCount(1);
            cart.setTotal_price(Double.parseDouble(goods.getPrice()));
            cart.setUserName(userName);
            cartService.addCart(cart);
        } else {
            // 购物车中已存在该商品，数量加1，更新总价
            Cart cart = cartList.get(0);
            cart.setCount(cart.getCount() + 1);
            cart.setTotal_price(cart.getTotal_price() + Double.parseDouble(goods.getPrice()));
            cartService.updateCart(cart);
        }

        // 扣减商品库存
        Goods updateGoods = new Goods();
        updateGoods.setStock(goods.getStock() - 1);
        QueryWrapper<Goods> updateWrapper = new QueryWrapper<>();
        updateWrapper.eq("id", goods.getId());
        goodsService.updateGoods(updateGoods, updateWrapper);

        // 根据来源标识决定跳转方式
        if ("1".equals(flag)) {
            // 从列表页添加，重定向到商品列表
            session.setAttribute("msg", "添加购物车成功！");
            modelAndView.setViewName("redirect:/goods/goodsList");
        } else {
            // 从详情页添加，返回商品详情页
            modelAndView.addObject("msg", "添加购物车成功！");
            QueryWrapper<Goods> detailWrapper = new QueryWrapper<>();
            detailWrapper.eq("id", goodsId);
            modelAndView.addObject("goodsList", goodsService.findGoods(detailWrapper));
            modelAndView.addObject("types", typeService.findTypes(null));
            
            // 查询购物车数量
            QueryWrapper<Cart> countWrapper = new QueryWrapper<>();
            countWrapper.eq("user_name", userName);
            long count = cartService.countCart(countWrapper);
            modelAndView.addObject("count", count);
            
            modelAndView.setViewName("goodsDetail");
        }

        return modelAndView;
    }

    /**
     * 购物车列表方法
     * 查询当前用户的购物车商品列表，支持分页显示，计算购物车总金额
     * @param request HttpServletRequest请求对象，包含page（页码）参数
     * @return ModelAndView 返回购物车列表页面
     */
    @RequestMapping("/cartList")
    public ModelAndView cartList(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        // 获取当前登录用户
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        // 未登录则跳转到登录页
        if (user == null) {
            modelAndView.addObject("msg", "请先登录！");
            modelAndView.setViewName("login");
            return modelAndView;
        }

        String userName = user.getUsername();
        
        // 从Session中获取并清除提示消息
        String msg = (String) session.getAttribute("msg");
        if (msg != null) {
            modelAndView.addObject("msg", msg);
            session.removeAttribute("msg");
        }
        
        // 分页参数处理
        String pageStr = request.getParameter("page");
        int page = pageStr != null ? Integer.parseInt(pageStr) : 1;
        int pageSize = 5;
        
        // 查询当前用户的购物车列表
        QueryWrapper<Cart> cartQueryWrapper = new QueryWrapper<>();
        cartQueryWrapper.eq("user_name", userName);
        List<Cart> cartList = cartService.findCart(cartQueryWrapper);
        
        // 计算购物车总金额，并更新每条记录的小计
        double totalAmount = 0;
        for (Cart cart : cartList) {
            double price = Double.parseDouble(cart.getGoodsPrice());
            int count = cart.getCount();
            double itemTotal = price * count;
            cart.setTotal_price(itemTotal);
            totalAmount += itemTotal;
        }
        
        // 计算分页信息
        int totalItems = cartList.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        
        // 截取当前页数据
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalItems);
        List<Cart> pageList = cartList.subList(startIndex, endIndex);
        
        // 将数据存入Model
        modelAndView.addObject("cartList", pageList);
        modelAndView.addObject("totalAmount", totalAmount);
        modelAndView.addObject("totalItems", totalItems);
        modelAndView.addObject("totalPages", totalPages);
        modelAndView.addObject("currentPage", page);
        modelAndView.setViewName("cartList");
        
        return modelAndView;
    }

    /**
     * 删除购物车商品方法
     * 删除指定的购物车记录，并将对应商品的库存加回
     * @param request HttpServletRequest请求对象，包含id（购物车记录ID）参数
     * @return ModelAndView 重定向到购物车列表页面
     */
    @RequestMapping("/deleteCart")
    public ModelAndView deleteCart(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        // 获取购物车记录ID
        String cartId = request.getParameter("id");
        
        // 查询购物车记录
        QueryWrapper<Cart> cartQueryWrapper = new QueryWrapper<>();
        cartQueryWrapper.eq("id", Integer.parseInt(cartId));
        List<Cart> cartList = cartService.findCart(cartQueryWrapper);
        
        if (!cartList.isEmpty()) {
            Cart cart = cartList.get(0);
            int goodId = cart.getGoodId();
            int count = cart.getCount();
            
            // 将商品库存加回
            Goods updateGoods = new Goods();
            QueryWrapper<Goods> goodsQueryWrapper = new QueryWrapper<>();
            goodsQueryWrapper.eq("id", goodId);
            List<Goods> goodsList = goodsService.findGoods(goodsQueryWrapper);
            
            if (!goodsList.isEmpty()) {
                Goods goods = goodsList.get(0);
                updateGoods.setStock(goods.getStock() + count);
                goodsService.updateGoods(updateGoods, goodsQueryWrapper);
            }
            
            // 删除购物车记录
            cartService.deleteCart(Integer.parseInt(cartId));
        }
        
        // 设置成功提示并跳转
        request.getSession().setAttribute("msg", "删除成功！");
        modelAndView.setViewName("redirect:/cart/cartList");
        
        return modelAndView;
    }

    /**
     * 修改购物车商品数量方法
     * 增加或减少购物车中商品的数量，同时更新商品库存
     * @param request HttpServletRequest请求对象，包含id（购物车记录ID）、delta（数量变化量，正数增加，负数减少）参数
     * @return ModelAndView 重定向到购物车列表页面
     */
    @RequestMapping("/changeCount")
    public ModelAndView changeCount(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        // 获取购物车记录ID和数量变化量
        String cartId = request.getParameter("id");
        int delta = Integer.parseInt(request.getParameter("delta"));
        
        // 查询购物车记录
        QueryWrapper<Cart> cartQueryWrapper = new QueryWrapper<>();
        cartQueryWrapper.eq("id", Integer.parseInt(cartId));
        List<Cart> cartList = cartService.findCart(cartQueryWrapper);
        
        if (!cartList.isEmpty()) {
            Cart cart = cartList.get(0);
            int newCount = cart.getCount() + delta;
            
            // 数量不能小于1
            if (newCount < 1) {
                request.getSession().setAttribute("msg", "商品数量最小为一");
            } else {
                int goodId = cart.getGoodId();
                Goods updateGoods = new Goods();
                QueryWrapper<Goods> goodsQueryWrapper = new QueryWrapper<>();
                goodsQueryWrapper.eq("id", goodId);
                List<Goods> goodsList = goodsService.findGoods(goodsQueryWrapper);
                
                if (!goodsList.isEmpty()) {
                    Goods goods = goodsList.get(0);
                    
                    if (delta > 0) {
                        // 增加数量：先检查库存是否充足
                        if (goods.getStock() <= 0) {
                            request.getSession().setAttribute("msg", "库存不足请叫管理员加货");
                            modelAndView.setViewName("redirect:/cart/cartList");
                            return modelAndView;
                        }
                        
                        // 扣减库存
                        updateGoods.setStock(goods.getStock() - 1);
                        goodsService.updateGoods(updateGoods, goodsQueryWrapper);
                        
                        // 更新购物车数量和小计
                        double price = Double.parseDouble(cart.getGoodsPrice());
                        cart.setCount(newCount);
                        cart.setTotal_price(price * newCount);
                        cartService.updateCart(cart);
                    } else {
                        // 减少数量：增加库存（delta为负数，-delta为正数）
                        updateGoods.setStock(goods.getStock() - delta);
                        goodsService.updateGoods(updateGoods, goodsQueryWrapper);
                        
                        // 更新购物车数量和小计
                        double price = Double.parseDouble(cart.getGoodsPrice());
                        cart.setCount(newCount);
                        cart.setTotal_price(price * newCount);
                        cartService.updateCart(cart);
                    }
                }
            }
        }
        
        modelAndView.setViewName("redirect:/cart/cartList");
        
        return modelAndView;
    }
}