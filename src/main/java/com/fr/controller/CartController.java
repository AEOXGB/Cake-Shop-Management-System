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

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    CartService cartService;

    @Autowired
    GoodsService goodsService;
    
    @Autowired
    TypeService typeService;

    @RequestMapping("/addToCart")
    public ModelAndView addToCart(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        String goodsId = request.getParameter("id");
        String flag = request.getParameter("flag");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            modelAndView.addObject("msg", "请先登录！");
            modelAndView.setViewName("login");
            return modelAndView;
        }

        String userName = user.getUsername();

        QueryWrapper<Goods> goodsQueryWrapper = new QueryWrapper<>();
        goodsQueryWrapper.eq("id", Integer.parseInt(goodsId));
        List<Goods> goodsList = goodsService.findGoods(goodsQueryWrapper);
        
        if (goodsList.isEmpty()) {
            modelAndView.addObject("msg", "商品不存在！");
            modelAndView.setViewName("index");
            return modelAndView;
        }

        Goods goods = goodsList.get(0);
        
        if (goods.getStock() <= 0) {
            modelAndView.addObject("msg", "库存不足请叫管理员加货");
            
            QueryWrapper<Cart> countWrapper = new QueryWrapper<>();
            countWrapper.eq("user_name", userName);
            long count = cartService.countCart(countWrapper);
            modelAndView.addObject("count", count);
            
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

        QueryWrapper<Cart> cartQueryWrapper = new QueryWrapper<>();
        cartQueryWrapper.eq("good_id", goodsId);
        cartQueryWrapper.eq("user_name", userName);
        List<Cart> cartList = cartService.findCart(cartQueryWrapper);

        

        if (cartList.isEmpty()) {
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
            Cart cart = cartList.get(0);
            cart.setCount(cart.getCount() + 1);
            cart.setTotal_price(cart.getTotal_price() + Double.parseDouble(goods.getPrice()));
            cartService.updateCart(cart);
        }

        Goods updateGoods = new Goods();
        updateGoods.setStock(goods.getStock() - 1);
        QueryWrapper<Goods> updateWrapper = new QueryWrapper<>();
        updateWrapper.eq("id", goods.getId());
        goodsService.updateGoods(updateGoods, updateWrapper);

        if ("1".equals(flag)) {
            session.setAttribute("msg", "添加购物车成功！");
            modelAndView.setViewName("redirect:/goods/goodsList");
        } else {
            modelAndView.addObject("msg", "添加购物车成功！");
            QueryWrapper<Goods> detailWrapper = new QueryWrapper<>();
            detailWrapper.eq("id", goodsId);
            modelAndView.addObject("goodsList", goodsService.findGoods(detailWrapper));
            modelAndView.addObject("types", typeService.findTypes(null));
            
            QueryWrapper<Cart> countWrapper = new QueryWrapper<>();
            countWrapper.eq("user_name", userName);
            long count = cartService.countCart(countWrapper);
            modelAndView.addObject("count", count);
            
            modelAndView.setViewName("goodsDetail");
        }

        return modelAndView;
    }

    @RequestMapping("/cartList")
    public ModelAndView cartList(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            modelAndView.addObject("msg", "请先登录！");
            modelAndView.setViewName("login");
            return modelAndView;
        }

        String userName = user.getUsername();
        
        String msg = (String) session.getAttribute("msg");
        if (msg != null) {
            modelAndView.addObject("msg", msg);
            session.removeAttribute("msg");
        }
        
        String pageStr = request.getParameter("page");
        int page = pageStr != null ? Integer.parseInt(pageStr) : 1;
        int pageSize = 5;
        
        QueryWrapper<Cart> cartQueryWrapper = new QueryWrapper<>();
        cartQueryWrapper.eq("user_name", userName);
        List<Cart> cartList = cartService.findCart(cartQueryWrapper);
        
        double totalAmount = 0;
        for (Cart cart : cartList) {
            double price = Double.parseDouble(cart.getGoodsPrice());
            int count = cart.getCount();
            double itemTotal = price * count;
            cart.setTotal_price(itemTotal);
            totalAmount += itemTotal;
        }
        
        int totalItems = cartList.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalItems);
        List<Cart> pageList = cartList.subList(startIndex, endIndex);
        
        modelAndView.addObject("cartList", pageList);
        modelAndView.addObject("totalAmount", totalAmount);
        modelAndView.addObject("totalItems", totalItems);
        modelAndView.addObject("totalPages", totalPages);
        modelAndView.addObject("currentPage", page);
        modelAndView.setViewName("cartList");
        
        return modelAndView;
    }

    @RequestMapping("/deleteCart")
    public ModelAndView deleteCart(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        String cartId = request.getParameter("id");
        
        QueryWrapper<Cart> cartQueryWrapper = new QueryWrapper<>();
        cartQueryWrapper.eq("id", Integer.parseInt(cartId));
        List<Cart> cartList = cartService.findCart(cartQueryWrapper);
        
        if (!cartList.isEmpty()) {
            Cart cart = cartList.get(0);
            int goodId = cart.getGoodId();
            int count = cart.getCount();
            
            Goods updateGoods = new Goods();
            QueryWrapper<Goods> goodsQueryWrapper = new QueryWrapper<>();
            goodsQueryWrapper.eq("id", goodId);
            List<Goods> goodsList = goodsService.findGoods(goodsQueryWrapper);
            
            if (!goodsList.isEmpty()) {
                Goods goods = goodsList.get(0);
                updateGoods.setStock(goods.getStock() + count);
                goodsService.updateGoods(updateGoods, goodsQueryWrapper);
            }
            
            cartService.deleteCart(Integer.parseInt(cartId));
        }
        
        request.getSession().setAttribute("msg", "删除成功！");
        modelAndView.setViewName("redirect:/cart/cartList");
        
        return modelAndView;
    }

    @RequestMapping("/changeCount")
    public ModelAndView changeCount(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        String cartId = request.getParameter("id");
        int delta = Integer.parseInt(request.getParameter("delta"));
        
        QueryWrapper<Cart> cartQueryWrapper = new QueryWrapper<>();
        cartQueryWrapper.eq("id", Integer.parseInt(cartId));
        List<Cart> cartList = cartService.findCart(cartQueryWrapper);
        
        if (!cartList.isEmpty()) {
            Cart cart = cartList.get(0);
            int newCount = cart.getCount() + delta;
            
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
                        if (goods.getStock() <= 0) {
                            request.getSession().setAttribute("msg", "库存不足请叫管理员加货");
                            modelAndView.setViewName("redirect:/cart/cartList");
                            return modelAndView;
                        }
                        
                        updateGoods.setStock(goods.getStock() - 1);
                        goodsService.updateGoods(updateGoods, goodsQueryWrapper);
                        
                        double price = Double.parseDouble(cart.getGoodsPrice());
                        cart.setCount(newCount);
                        cart.setTotal_price(price * newCount);
                        cartService.updateCart(cart);
                    } else {
                        updateGoods.setStock(goods.getStock() - delta);
                        goodsService.updateGoods(updateGoods, goodsQueryWrapper);
                        
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