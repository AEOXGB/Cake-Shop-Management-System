package com.fr.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fr.javaBean.Goods;
import com.fr.javaBean.Order;
import com.fr.javaBean.OrderItem;
import com.fr.javaBean.Rider;
import com.fr.javaBean.Notification;
import com.fr.mapper.GoodsMapper;
import com.fr.mapper.NotificationMapper;
import com.fr.mapper.OrderItemMapper;
import com.fr.mapper.OrderMapper;
import com.fr.service.RiderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 骑手控制器
 * 所属模块：骑手管理模块
 * 处理骑手相关的请求，包括骑手登录、订单列表、接单、取货、配送、完成、个人中心、通知、收入统计等功能
 * 请求路径前缀：/rider
 */
@Controller
@RequestMapping("/rider")
public class RiderController {

    @Autowired
    private RiderService riderService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    /**
     * 骑手登录页面方法
     * 跳转到骑手登录页面
     * @return ModelAndView 返回骑手登录页面
     */
    @RequestMapping("/login")
    public ModelAndView riderLogin() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("rider/riderLogin");
        return modelAndView;
    }

    /**
     * 骑手登录验证方法
     * 验证骑手手机号和密码，登录成功将骑手信息存入Session
     * @param phone 骑手手机号
     * @param password 骑手密码
     * @param session HttpSession会话对象
     * @return ModelAndView 登录成功跳转到骑手首页，失败返回登录页并提示错误
     */
    @RequestMapping("/doLogin")
    public ModelAndView doLogin(@RequestParam String phone, @RequestParam String password, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        // 调用服务层验证骑手登录
        Rider rider = riderService.login(phone, password);
        if (rider != null) {
            // 登录成功，将骑手信息存入Session
            session.setAttribute("rider", rider);
            modelAndView.setViewName("redirect:/rider/index");
        } else {
            // 登录失败，返回错误提示
            modelAndView.addObject("error", "手机号或密码错误");
            modelAndView.setViewName("rider/riderLogin");
        }
        return modelAndView;
    }

    /**
     * 骑手退出登录方法
     * 清除Session中的骑手信息，跳转到登录页面
     * @param session HttpSession会话对象
     * @return ModelAndView 重定向到骑手登录页面
     */
    @RequestMapping("/logout")
    public ModelAndView logout(HttpSession session) {
        // 从Session中移除骑手信息
        session.removeAttribute("rider");
        ModelAndView modelAndView = new ModelAndView("redirect:/rider/login");
        return modelAndView;
    }

    /**
     * 骑手首页方法
     * 显示骑手工作台，统计待接单、待取货、配送中、已完成订单数量和总佣金
     * @param request HttpServletRequest请求对象
     * @return ModelAndView 返回骑手首页
     */
    @RequestMapping("/index")
    public ModelAndView riderIndex(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        // 获取当前登录骑手
        Rider rider = (Rider) request.getSession().getAttribute("rider");
        if (rider == null) {
            modelAndView.setViewName("redirect:/rider/login");
            return modelAndView;
        }

        // 统计待配送订单数（状态3：待配送）
        QueryWrapper<Order> pendingQuery = new QueryWrapper<>();
        pendingQuery.eq("status", 3);
        long pendingCount = orderMapper.selectCount(pendingQuery);
        List<Order> pendingOrders = orderMapper.selectList(pendingQuery);

        // 统计待取货订单数（状态6：待取货）
        QueryWrapper<Order> pickupQuery = new QueryWrapper<>();
        pickupQuery.eq("status", 6).eq("rider_id", rider.getId());
        long pickupCount = orderMapper.selectCount(pickupQuery);

        // 统计配送中订单数（状态7：配送中）
        QueryWrapper<Order> deliveringQuery = new QueryWrapper<>();
        deliveringQuery.eq("status", 7).eq("rider_id", rider.getId());
        long deliveringCount = orderMapper.selectCount(deliveringQuery);

        // 统计已完成订单数（状态4：已完成）
        QueryWrapper<Order> completedQuery = new QueryWrapper<>();
        completedQuery.eq("status", 4).eq("rider_id", rider.getId());
        long completedCount = orderMapper.selectCount(completedQuery);

        // 计算已完成订单的总佣金
        List<Order> completedOrders = orderMapper.selectList(completedQuery);
        double totalCommission = 0.0;
        for (Order order : completedOrders) {
            if (order.getCommission() != null) {
                totalCommission += order.getCommission();
            }
        }

        modelAndView.addObject("pendingCount", pendingCount);
        modelAndView.addObject("pickupCount", pickupCount);
        modelAndView.addObject("deliveringCount", deliveringCount);
        modelAndView.addObject("completedCount", completedCount);
        modelAndView.addObject("totalCommission", totalCommission);
        modelAndView.addObject("orders", pendingOrders);
        modelAndView.addObject("rider", rider);
        modelAndView.setViewName("rider/riderIndex");
        return modelAndView;
    }

    /**
     * 待接单列表方法
     * 显示所有待配送的订单，骑手可以选择接单
     * @param request HttpServletRequest请求对象
     * @return ModelAndView 返回待接单列表页面
     */
    @RequestMapping("/pendingOrders")
    public ModelAndView pendingOrders(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        // 获取当前登录骑手
        Rider rider = (Rider) request.getSession().getAttribute("rider");
        if (rider == null) {
            modelAndView.setViewName("redirect:/rider/login");
            return modelAndView;
        }

        // 查询待配送订单（状态3：待配送）
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 3).orderByDesc("datetime");
        List<Order> orderList = orderMapper.selectList(queryWrapper);
        long pendingCount = orderList.size();

        // 统计待取货订单数
        QueryWrapper<Order> pickupQuery = new QueryWrapper<>();
        pickupQuery.eq("status", 6).eq("rider_id", rider.getId());
        long pickupCount = orderMapper.selectCount(pickupQuery);

        // 统计配送中订单数
        QueryWrapper<Order> deliveringQuery = new QueryWrapper<>();
        deliveringQuery.eq("status", 7).eq("rider_id", rider.getId());
        long deliveringCount = orderMapper.selectCount(deliveringQuery);

        // 统计已完成订单数
        QueryWrapper<Order> completedQuery = new QueryWrapper<>();
        completedQuery.eq("status", 4).eq("rider_id", rider.getId());
        long completedCount = orderMapper.selectCount(completedQuery);

        // 计算已完成订单的总佣金
        List<Order> completedOrders = orderMapper.selectList(completedQuery);
        double totalCommission = 0.0;
        for (Order order : completedOrders) {
            if (order.getCommission() != null) {
                totalCommission += order.getCommission();
            }
        }

        // 组装订单数据
        List<Map<String, Object>> orders = new ArrayList<>();
        for (Order order : orderList) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", order.getId());
            map.put("total", order.getTotal());
            map.put("name", order.getName());
            map.put("phone", order.getPhone());
            map.put("address", order.getAddress());
            map.put("datetime", order.getDatetime());
            orders.add(map);
        }

        modelAndView.addObject("pendingCount", pendingCount);
        modelAndView.addObject("pickupCount", pickupCount);
        modelAndView.addObject("deliveringCount", deliveringCount);
        modelAndView.addObject("completedCount", completedCount);
        modelAndView.addObject("totalCommission", totalCommission);
        modelAndView.addObject("orders", orders);
        modelAndView.addObject("rider", rider);
        modelAndView.setViewName("rider/pendingOrders");
        return modelAndView;
    }

    /**
     * 接单方法
     * 骑手接取待配送订单，将订单状态改为待取货并分配给当前骑手
     * @param id 订单ID
     * @param request HttpServletRequest请求对象
     * @return ModelAndView 重定向到待取货订单列表
     */
    @RequestMapping("/acceptOrder")
    public ModelAndView acceptOrder(@RequestParam long id, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        // 获取当前登录骑手
        Rider rider = (Rider) request.getSession().getAttribute("rider");
        if (rider == null) {
            modelAndView.setViewName("redirect:/rider/login");
            return modelAndView;
        }

        // 查询订单并更新状态为待取货（状态6）
        Order order = orderMapper.selectById(id);
        if (order != null && order.getStatus() == 3) {
            order.setStatus(6);
            order.setRiderId(rider.getId());
            order.setPickupTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            orderMapper.updateById(order);
        }

        modelAndView.setViewName("redirect:/rider/pickupOrders");
        return modelAndView;
    }

    /**
     * 待取货订单列表方法
     * 显示骑手已接单但尚未取货的订单列表
     * @param request HttpServletRequest请求对象
     * @return ModelAndView 返回待取货订单列表页面
     */
    @RequestMapping("/pickupOrders")
    public ModelAndView pickupOrders(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        // 获取当前登录骑手
        Rider rider = (Rider) request.getSession().getAttribute("rider");
        if (rider == null) {
            modelAndView.setViewName("redirect:/rider/login");
            return modelAndView;
        }

        // 统计待接单数量
        QueryWrapper<Order> pendingQuery = new QueryWrapper<>();
        pendingQuery.eq("status", 3);
        long pendingCount = orderMapper.selectCount(pendingQuery);

        // 查询待取货订单（状态6：待取货）
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 6).eq("rider_id", rider.getId()).orderByDesc("datetime");
        List<Order> orderList = orderMapper.selectList(queryWrapper);
        long pickupCount = orderList.size();

        // 统计配送中订单数
        QueryWrapper<Order> deliveringQuery = new QueryWrapper<>();
        deliveringQuery.eq("status", 7).eq("rider_id", rider.getId());
        long deliveringCount = orderMapper.selectCount(deliveringQuery);

        // 统计已完成订单数
        QueryWrapper<Order> completedQuery = new QueryWrapper<>();
        completedQuery.eq("status", 4).eq("rider_id", rider.getId());
        long completedCount = orderMapper.selectCount(completedQuery);

        // 计算已完成订单的总佣金
        List<Order> completedOrders = orderMapper.selectList(completedQuery);
        double totalCommission = 0.0;
        for (Order order : completedOrders) {
            if (order.getCommission() != null) {
                totalCommission += order.getCommission();
            }
        }

        // 组装订单数据
        List<Map<String, Object>> orders = new ArrayList<>();
        for (Order order : orderList) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", order.getId());
            map.put("total", order.getTotal());
            map.put("name", order.getName());
            map.put("phone", order.getPhone());
            map.put("address", order.getAddress());
            map.put("datetime", order.getDatetime());
            orders.add(map);
        }

        modelAndView.addObject("pendingCount", pendingCount);
        modelAndView.addObject("pickupCount", pickupCount);
        modelAndView.addObject("deliveringCount", deliveringCount);
        modelAndView.addObject("completedCount", completedCount);
        modelAndView.addObject("totalCommission", totalCommission);
        modelAndView.addObject("orders", orders);
        modelAndView.addObject("rider", rider);
        modelAndView.setViewName("rider/pickupOrders");
        return modelAndView;
    }

    /**
     * 确认取货方法
     * 骑手确认已取货，将订单状态改为配送中
     * @param id 订单ID
     * @param request HttpServletRequest请求对象
     * @return ModelAndView 重定向到配送中订单列表
     */
    @RequestMapping("/confirmPickup")
    public ModelAndView confirmPickup(@RequestParam long id, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        // 获取当前登录骑手
        Rider rider = (Rider) request.getSession().getAttribute("rider");
        if (rider == null) {
            modelAndView.setViewName("redirect:/rider/login");
            return modelAndView;
        }

        // 查询订单并更新状态为配送中（状态7）
        Order order = orderMapper.selectById(id);
        if (order != null && order.getStatus() == 6) {
            order.setStatus(7);
            order.setDeliveryTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            orderMapper.updateById(order);
        }

        modelAndView.setViewName("redirect:/rider/deliveringOrders");
        return modelAndView;
    }

    /**
     * 配送中订单列表方法
     * 显示骑手正在配送中的订单列表
     * @param request HttpServletRequest请求对象
     * @return ModelAndView 返回配送中订单列表页面
     */
    @RequestMapping("/deliveringOrders")
    public ModelAndView deliveringOrders(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        // 获取当前登录骑手
        Rider rider = (Rider) request.getSession().getAttribute("rider");
        if (rider == null) {
            modelAndView.setViewName("redirect:/rider/login");
            return modelAndView;
        }

        // 统计待接单数量
        QueryWrapper<Order> pendingQuery = new QueryWrapper<>();
        pendingQuery.eq("status", 3);
        long pendingCount = orderMapper.selectCount(pendingQuery);

        // 统计待取货订单数
        QueryWrapper<Order> pickupQuery = new QueryWrapper<>();
        pickupQuery.eq("status", 6).eq("rider_id", rider.getId());
        long pickupCount = orderMapper.selectCount(pickupQuery);

        // 查询配送中订单（状态7：配送中）
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 7).eq("rider_id", rider.getId()).orderByDesc("datetime");
        List<Order> orderList = orderMapper.selectList(queryWrapper);
        long deliveringCount = orderList.size();

        // 统计已完成订单数
        QueryWrapper<Order> completedQuery = new QueryWrapper<>();
        completedQuery.eq("status", 4).eq("rider_id", rider.getId());
        long completedCount = orderMapper.selectCount(completedQuery);

        // 计算已完成订单的总佣金
        List<Order> completedOrders = orderMapper.selectList(completedQuery);
        double totalCommission = 0.0;
        for (Order order : completedOrders) {
            if (order.getCommission() != null) {
                totalCommission += order.getCommission();
            }
        }

        // 组装订单数据
        List<Map<String, Object>> orders = new ArrayList<>();
        for (Order order : orderList) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", order.getId());
            map.put("total", order.getTotal());
            map.put("name", order.getName());
            map.put("phone", order.getPhone());
            map.put("address", order.getAddress());
            map.put("datetime", order.getDatetime());
            orders.add(map);
        }

        modelAndView.addObject("pendingCount", pendingCount);
        modelAndView.addObject("pickupCount", pickupCount);
        modelAndView.addObject("deliveringCount", deliveringCount);
        modelAndView.addObject("completedCount", completedCount);
        modelAndView.addObject("totalCommission", totalCommission);
        modelAndView.addObject("orders", orders);
        modelAndView.addObject("rider", rider);
        modelAndView.setViewName("rider/deliveringOrders");
        return modelAndView;
    }

    /**
     * 确认配送完成方法
     * 骑手确认订单配送完成，将订单状态改为已完成
     * @param id 订单ID
     * @param request HttpServletRequest请求对象
     * @return ModelAndView 重定向到已完成订单列表
     */
    @RequestMapping("/confirmDeliver")
    public ModelAndView confirmDeliver(@RequestParam long id, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        // 获取当前登录骑手
        Rider rider = (Rider) request.getSession().getAttribute("rider");
        if (rider == null) {
            modelAndView.setViewName("redirect:/rider/login");
            return modelAndView;
        }

        // 查询订单并更新状态为已完成（状态4）
        Order order = orderMapper.selectById(id);
        if (order != null && order.getStatus() == 7) {
            order.setStatus(4);
            order.setCompleteTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            orderMapper.updateById(order);
        }

        modelAndView.setViewName("redirect:/rider/completedOrders");
        return modelAndView;
    }

    /**
     * 订单详情方法
     * 查看指定订单的详细信息，包括商品明细
     * @param id 订单ID
     * @param request HttpServletRequest请求对象
     * @return ModelAndView 返回订单详情页面
     */
    @RequestMapping("/orderDetail")
    public ModelAndView orderDetail(@RequestParam long id, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        // 获取当前登录骑手
        Rider rider = (Rider) request.getSession().getAttribute("rider");
        if (rider == null) {
            modelAndView.setViewName("redirect:/rider/login");
            return modelAndView;
        }

        // 查询订单信息
        Order order = orderMapper.selectById(id);
        
        // 查询订单项
        QueryWrapper<OrderItem> itemQuery = new QueryWrapper<>();
        itemQuery.eq("order_id", id);
        List<OrderItem> orderItems = orderItemMapper.selectList(itemQuery);
        
        // 组装商品明细
        List<Map<String, Object>> goodsList = new ArrayList<>();
        for (OrderItem item : orderItems) {
            Map<String, Object> goods = new HashMap<>();
            Goods goodsInfo = goodsMapper.selectById(item.getGoodsId());
            goods.put("name", goodsInfo != null ? goodsInfo.getName() : "未知商品");
            goods.put("price", item.getPrice());
            goods.put("amount", item.getAmount());
            goodsList.add(goods);
        }
        
        order.setGoods(goodsList);
        modelAndView.addObject("order", order);
        modelAndView.addObject("rider", rider);
        modelAndView.setViewName("rider/orderDetail");
        return modelAndView;
    }

    /**
     * 已完成订单列表方法
     * 显示骑手已完成的订单列表和总佣金
     * @param request HttpServletRequest请求对象
     * @return ModelAndView 返回已完成订单列表页面
     */
    @RequestMapping("/completedOrders")
    public ModelAndView completedOrders(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        // 获取当前登录骑手
        Rider rider = (Rider) request.getSession().getAttribute("rider");
        if (rider == null) {
            modelAndView.setViewName("redirect:/rider/login");
            return modelAndView;
        }

        // 统计待接单数量
        QueryWrapper<Order> pendingQuery = new QueryWrapper<>();
        pendingQuery.eq("status", 3);
        long pendingCount = orderMapper.selectCount(pendingQuery);

        // 统计待取货订单数
        QueryWrapper<Order> pickupQuery = new QueryWrapper<>();
        pickupQuery.eq("status", 6).eq("rider_id", rider.getId());
        long pickupCount = orderMapper.selectCount(pickupQuery);

        // 统计配送中订单数
        QueryWrapper<Order> deliveringQuery = new QueryWrapper<>();
        deliveringQuery.eq("status", 7).eq("rider_id", rider.getId());
        long deliveringCount = orderMapper.selectCount(deliveringQuery);

        // 查询已完成订单（状态4：已完成）
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 4).eq("rider_id", rider.getId()).orderByDesc("datetime");
        List<Order> orderList = orderMapper.selectList(queryWrapper);
        long completedCount = orderList.size();

        // 计算总佣金并组装订单数据
        double totalCommission = 0.0;
        List<Map<String, Object>> orders = new ArrayList<>();
        for (Order order : orderList) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", order.getId());
            map.put("total", order.getTotal());
            map.put("name", order.getName());
            map.put("phone", order.getPhone());
            map.put("address", order.getAddress());
            map.put("datetime", order.getDatetime());
            map.put("completeTime", order.getCompleteTime());
            orders.add(map);
            
            if (order.getCommission() != null) {
                totalCommission += order.getCommission();
            }
        }

        modelAndView.addObject("pendingCount", pendingCount);
        modelAndView.addObject("pickupCount", pickupCount);
        modelAndView.addObject("deliveringCount", deliveringCount);
        modelAndView.addObject("completedCount", completedCount);
        modelAndView.addObject("totalCommission", totalCommission);
        modelAndView.addObject("orders", orders);
        modelAndView.addObject("rider", rider);
        modelAndView.setViewName("rider/completedOrders");
        return modelAndView;
    }

    /**
     * 骑手个人中心方法
     * 显示骑手个人信息和配送统计（今日、本月、累计订单数和收入）
     * @param request HttpServletRequest请求对象
     * @return ModelAndView 返回骑手个人中心页面
     */
    @RequestMapping("/profile")
    public ModelAndView profile(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        // 获取当前登录骑手
        Rider rider = (Rider) request.getSession().getAttribute("rider");
        if (rider == null) {
            modelAndView.setViewName("redirect:/rider/login");
            return modelAndView;
        }

        // 统计今日完成订单数
        QueryWrapper<Order> todayQuery = new QueryWrapper<>();
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        todayQuery.eq("status", 4).eq("rider_id", rider.getId()).like("complete_time", today);
        long todayCount = orderMapper.selectCount(todayQuery);

        // 统计本月完成订单数
        QueryWrapper<Order> monthQuery = new QueryWrapper<>();
        String month = new SimpleDateFormat("yyyy-MM").format(new Date());
        monthQuery.eq("status", 4).eq("rider_id", rider.getId()).like("complete_time", month);
        long monthCount = orderMapper.selectCount(monthQuery);

        // 统计累计完成订单数和总收入
        QueryWrapper<Order> totalQuery = new QueryWrapper<>();
        totalQuery.eq("status", 4).eq("rider_id", rider.getId());
        List<Order> completedOrders = orderMapper.selectList(totalQuery);
        double totalIncome = 0;
        for (Order order : completedOrders) {
            totalIncome += order.getCommission() != null ? order.getCommission() : 0;
        }

        modelAndView.addObject("rider", rider);
        modelAndView.addObject("todayCount", todayCount);
        modelAndView.addObject("monthCount", monthCount);
        modelAndView.addObject("totalOrders", completedOrders.size());
        modelAndView.addObject("totalIncome", totalIncome);
        modelAndView.setViewName("rider/riderProfile");
        return modelAndView;
    }

    /**
     * 骑手通知列表方法
     * 显示骑手的通知消息，支持按类型筛选
     * @param type 通知类型（all全部/order订单/system系统）
     * @param request HttpServletRequest请求对象
     * @return ModelAndView 返回通知列表页面
     */
    @RequestMapping("/notifications")
    public ModelAndView notifications(@RequestParam(defaultValue = "all") String type, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        // 获取当前登录骑手
        Rider rider = (Rider) request.getSession().getAttribute("rider");
        if (rider == null) {
            modelAndView.setViewName("redirect:/rider/login");
            return modelAndView;
        }

        List<Notification> notifications = new ArrayList<>();
        Integer unreadCount = 0;

        try {
            // 根据类型查询通知
            if ("all".equals(type)) {
                notifications = notificationMapper.selectByRiderId(rider.getId());
            } else {
                notifications = notificationMapper.selectByRiderIdAndType(rider.getId(), type);
            }

            // 格式化通知时间
            for (Notification notification : notifications) {
                notification.setTime(formatTime(notification.getCreateTime()));
            }

            // 统计未读消息数
            unreadCount = notificationMapper.countUnread(rider.getId());
        } catch (Exception e) {
        }

        modelAndView.addObject("rider", rider);
        modelAndView.addObject("notifications", notifications);
        modelAndView.addObject("unreadCount", unreadCount);
        modelAndView.setViewName("rider/riderNotifications");
        return modelAndView;
    }

    /**
     * 格式化时间为友好显示文本
     * 将时间字符串转换为"刚刚"、"x分钟前"、"x小时前"、"x天前"等友好格式
     * @param createTime 原始时间字符串
     * @return 格式化后的时间文本
     */
    private String formatTime(String createTime) {
        if (createTime == null || createTime.isEmpty()) {
            return "";
        }
        try {
            Date time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(createTime);
            Date now = new Date();
            long diff = now.getTime() - time.getTime();
            long minutes = diff / (1000 * 60);
            long hours = diff / (1000 * 60 * 60);
            long days = diff / (1000 * 60 * 60 * 24);

            // 根据时间差返回不同的友好格式
            if (minutes < 1) {
                return "刚刚";
            } else if (minutes < 60) {
                return minutes + "分钟前";
            } else if (hours < 24) {
                return hours + "小时前";
            } else if (days < 7) {
                return days + "天前";
            } else {
                return createTime.substring(5, 16);
            }
        } catch (Exception e) {
            return createTime;
        }
    }

    /**
     * 骑手收入统计方法
     * 显示骑手的收入统计，支持按今日、本月、上月筛选
     * @param type 统计类型（all全部/today今日/month本月/lastMonth上月）
     * @param request HttpServletRequest请求对象
     * @return ModelAndView 返回收入统计页面
     */
    @RequestMapping("/income")
    public ModelAndView income(@RequestParam(defaultValue = "all") String type, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        // 获取当前登录骑手
        Rider rider = (Rider) request.getSession().getAttribute("rider");
        if (rider == null) {
            modelAndView.setViewName("redirect:/rider/login");
            return modelAndView;
        }

        // 获取日期字符串
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String month = new SimpleDateFormat("yyyy-MM").format(new Date());
        String lastMonth = new SimpleDateFormat("yyyy-MM").format(new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000));

        // 查询已完成订单
        QueryWrapper<Order> completedQuery = new QueryWrapper<>();
        completedQuery.eq("status", 4).eq("rider_id", rider.getId());
        
        // 根据类型筛选
        if ("today".equals(type)) {
            completedQuery.like("complete_time", today);
        } else if ("month".equals(type)) {
            completedQuery.like("complete_time", month);
        } else if ("lastMonth".equals(type)) {
            completedQuery.like("complete_time", lastMonth);
        }
        
        completedQuery.orderByDesc("complete_time");
        List<Order> completedOrders = orderMapper.selectList(completedQuery);

        double totalIncome = 0;
        double monthIncome = 0;
        double todayIncome = 0;
        
        List<Map<String, Object>> incomeList = new ArrayList<>();
        
        // 计算收入并组装数据
        for (Order order : completedOrders) {
            double commission = order.getCommission() != null ? order.getCommission() : 0;
            totalIncome += commission;
            
            String completeTime = order.getCompleteTime() != null ? order.getCompleteTime() : "";
            if (completeTime.startsWith(month)) {
                monthIncome += commission;
            }
            if (completeTime.startsWith(today)) {
                todayIncome += commission;
            }
            
            Map<String, Object> item = new HashMap<>();
            item.put("orderId", order.getId());
            item.put("commission", commission);
            item.put("time", completeTime);
            incomeList.add(item);
        }

        modelAndView.addObject("rider", rider);
        modelAndView.addObject("totalIncome", String.format("%.2f", totalIncome));
        modelAndView.addObject("monthIncome", String.format("%.2f", monthIncome));
        modelAndView.addObject("todayIncome", String.format("%.2f", todayIncome));
        modelAndView.addObject("incomeList", incomeList);
        modelAndView.setViewName("rider/riderIncome");
        return modelAndView;
    }

    /**
     * 骑手设置页面方法
     * 跳转到骑手设置页面
     * @param request HttpServletRequest请求对象
     * @return ModelAndView 返回骑手设置页面
     */
    @RequestMapping("/settings")
    public ModelAndView settings(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        // 获取当前登录骑手
        Rider rider = (Rider) request.getSession().getAttribute("rider");
        if (rider == null) {
            modelAndView.setViewName("redirect:/rider/login");
            return modelAndView;
        }
        modelAndView.addObject("rider", rider);
        modelAndView.setViewName("rider/riderSettings");
        return modelAndView;
    }

    /**
     * 修改骑手密码方法
     * 验证原密码，修改骑手登录密码
     * @param request HttpServletRequest请求对象，包含oldPassword（原密码）、newPassword（新密码）、confirmPassword（确认密码）参数
     * @return ModelAndView 返回设置页面并提示修改结果
     */
    @RequestMapping("/changePassword")
    public ModelAndView changePassword(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        // 获取当前登录骑手
        Rider rider = (Rider) request.getSession().getAttribute("rider");
        if (rider == null) {
            modelAndView.setViewName("redirect:/rider/login");
            return modelAndView;
        }

        // 获取密码参数
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // 验证原密码
        if (!rider.getPassword().equals(oldPassword)) {
            modelAndView.addObject("msg", "原密码错误！");
            modelAndView.addObject("rider", rider);
            modelAndView.setViewName("rider/riderSettings");
            return modelAndView;
        }

        // 验证两次输入的新密码是否一致
        if (!newPassword.equals(confirmPassword)) {
            modelAndView.addObject("msg", "两次输入的新密码不一致！");
            modelAndView.addObject("rider", rider);
            modelAndView.setViewName("rider/riderSettings");
            return modelAndView;
        }

        // 更新密码
        rider.setPassword(newPassword);
        riderService.updateRider(rider);

        modelAndView.addObject("msg", "密码修改成功！");
        modelAndView.addObject("rider", rider);
        modelAndView.setViewName("rider/riderSettings");
        return modelAndView;
    }
}