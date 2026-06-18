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

    @RequestMapping("/login")
    public ModelAndView riderLogin() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("rider/riderLogin");
        return modelAndView;
    }

    @RequestMapping("/doLogin")
    public ModelAndView doLogin(@RequestParam String phone, @RequestParam String password, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        Rider rider = riderService.login(phone, password);
        if (rider != null) {
            session.setAttribute("rider", rider);
            modelAndView.setViewName("redirect:/rider/index");
        } else {
            modelAndView.addObject("error", "手机号或密码错误");
            modelAndView.setViewName("rider/riderLogin");
        }
        return modelAndView;
    }

    @RequestMapping("/logout")
    public ModelAndView logout(HttpSession session) {
        session.removeAttribute("rider");
        ModelAndView modelAndView = new ModelAndView("redirect:/rider/login");
        return modelAndView;
    }

    @RequestMapping("/index")
    public ModelAndView riderIndex(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        Rider rider = (Rider) request.getSession().getAttribute("rider");
        if (rider == null) {
            modelAndView.setViewName("redirect:/rider/login");
            return modelAndView;
        }

        QueryWrapper<Order> pendingQuery = new QueryWrapper<>();
        pendingQuery.eq("status", 3);
        long pendingCount = orderMapper.selectCount(pendingQuery);
        List<Order> pendingOrders = orderMapper.selectList(pendingQuery);

        QueryWrapper<Order> pickupQuery = new QueryWrapper<>();
        pickupQuery.eq("status", 6).eq("rider_id", rider.getId());
        long pickupCount = orderMapper.selectCount(pickupQuery);

        QueryWrapper<Order> deliveringQuery = new QueryWrapper<>();
        deliveringQuery.eq("status", 7).eq("rider_id", rider.getId());
        long deliveringCount = orderMapper.selectCount(deliveringQuery);

        QueryWrapper<Order> completedQuery = new QueryWrapper<>();
        completedQuery.eq("status", 4).eq("rider_id", rider.getId());
        long completedCount = orderMapper.selectCount(completedQuery);

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

    @RequestMapping("/pendingOrders")
    public ModelAndView pendingOrders(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        Rider rider = (Rider) request.getSession().getAttribute("rider");
        if (rider == null) {
            modelAndView.setViewName("redirect:/rider/login");
            return modelAndView;
        }

        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 3).orderByDesc("datetime");
        List<Order> orderList = orderMapper.selectList(queryWrapper);
        long pendingCount = orderList.size();

        QueryWrapper<Order> pickupQuery = new QueryWrapper<>();
        pickupQuery.eq("status", 6).eq("rider_id", rider.getId());
        long pickupCount = orderMapper.selectCount(pickupQuery);

        QueryWrapper<Order> deliveringQuery = new QueryWrapper<>();
        deliveringQuery.eq("status", 7).eq("rider_id", rider.getId());
        long deliveringCount = orderMapper.selectCount(deliveringQuery);

        QueryWrapper<Order> completedQuery = new QueryWrapper<>();
        completedQuery.eq("status", 4).eq("rider_id", rider.getId());
        long completedCount = orderMapper.selectCount(completedQuery);

        List<Order> completedOrders = orderMapper.selectList(completedQuery);
        double totalCommission = 0.0;
        for (Order order : completedOrders) {
            if (order.getCommission() != null) {
                totalCommission += order.getCommission();
            }
        }

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

    @RequestMapping("/acceptOrder")
    public ModelAndView acceptOrder(@RequestParam long id, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        Rider rider = (Rider) request.getSession().getAttribute("rider");
        if (rider == null) {
            modelAndView.setViewName("redirect:/rider/login");
            return modelAndView;
        }

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

    @RequestMapping("/pickupOrders")
    public ModelAndView pickupOrders(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        Rider rider = (Rider) request.getSession().getAttribute("rider");
        if (rider == null) {
            modelAndView.setViewName("redirect:/rider/login");
            return modelAndView;
        }

        QueryWrapper<Order> pendingQuery = new QueryWrapper<>();
        pendingQuery.eq("status", 3);
        long pendingCount = orderMapper.selectCount(pendingQuery);

        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 6).eq("rider_id", rider.getId()).orderByDesc("datetime");
        List<Order> orderList = orderMapper.selectList(queryWrapper);
        long pickupCount = orderList.size();

        QueryWrapper<Order> deliveringQuery = new QueryWrapper<>();
        deliveringQuery.eq("status", 7).eq("rider_id", rider.getId());
        long deliveringCount = orderMapper.selectCount(deliveringQuery);

        QueryWrapper<Order> completedQuery = new QueryWrapper<>();
        completedQuery.eq("status", 4).eq("rider_id", rider.getId());
        long completedCount = orderMapper.selectCount(completedQuery);

        List<Order> completedOrders = orderMapper.selectList(completedQuery);
        double totalCommission = 0.0;
        for (Order order : completedOrders) {
            if (order.getCommission() != null) {
                totalCommission += order.getCommission();
            }
        }

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

    @RequestMapping("/confirmPickup")
    public ModelAndView confirmPickup(@RequestParam long id, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        Rider rider = (Rider) request.getSession().getAttribute("rider");
        if (rider == null) {
            modelAndView.setViewName("redirect:/rider/login");
            return modelAndView;
        }

        Order order = orderMapper.selectById(id);
        if (order != null && order.getStatus() == 6) {
            order.setStatus(7);
            order.setDeliveryTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            orderMapper.updateById(order);
        }

        modelAndView.setViewName("redirect:/rider/deliveringOrders");
        return modelAndView;
    }

    @RequestMapping("/deliveringOrders")
    public ModelAndView deliveringOrders(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        Rider rider = (Rider) request.getSession().getAttribute("rider");
        if (rider == null) {
            modelAndView.setViewName("redirect:/rider/login");
            return modelAndView;
        }

        QueryWrapper<Order> pendingQuery = new QueryWrapper<>();
        pendingQuery.eq("status", 3);
        long pendingCount = orderMapper.selectCount(pendingQuery);

        QueryWrapper<Order> pickupQuery = new QueryWrapper<>();
        pickupQuery.eq("status", 6).eq("rider_id", rider.getId());
        long pickupCount = orderMapper.selectCount(pickupQuery);

        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 7).eq("rider_id", rider.getId()).orderByDesc("datetime");
        List<Order> orderList = orderMapper.selectList(queryWrapper);
        long deliveringCount = orderList.size();

        QueryWrapper<Order> completedQuery = new QueryWrapper<>();
        completedQuery.eq("status", 4).eq("rider_id", rider.getId());
        long completedCount = orderMapper.selectCount(completedQuery);

        List<Order> completedOrders = orderMapper.selectList(completedQuery);
        double totalCommission = 0.0;
        for (Order order : completedOrders) {
            if (order.getCommission() != null) {
                totalCommission += order.getCommission();
            }
        }

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

    @RequestMapping("/confirmDeliver")
    public ModelAndView confirmDeliver(@RequestParam long id, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        Rider rider = (Rider) request.getSession().getAttribute("rider");
        if (rider == null) {
            modelAndView.setViewName("redirect:/rider/login");
            return modelAndView;
        }

        Order order = orderMapper.selectById(id);
        if (order != null && order.getStatus() == 7) {
            order.setStatus(4);
            order.setCompleteTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            orderMapper.updateById(order);
        }

        modelAndView.setViewName("redirect:/rider/completedOrders");
        return modelAndView;
    }

    @RequestMapping("/orderDetail")
    public ModelAndView orderDetail(@RequestParam long id, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        Rider rider = (Rider) request.getSession().getAttribute("rider");
        if (rider == null) {
            modelAndView.setViewName("redirect:/rider/login");
            return modelAndView;
        }

        Order order = orderMapper.selectById(id);
        
        QueryWrapper<OrderItem> itemQuery = new QueryWrapper<>();
        itemQuery.eq("order_id", id);
        List<OrderItem> orderItems = orderItemMapper.selectList(itemQuery);
        
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

    @RequestMapping("/completedOrders")
    public ModelAndView completedOrders(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        Rider rider = (Rider) request.getSession().getAttribute("rider");
        if (rider == null) {
            modelAndView.setViewName("redirect:/rider/login");
            return modelAndView;
        }

        QueryWrapper<Order> pendingQuery = new QueryWrapper<>();
        pendingQuery.eq("status", 3);
        long pendingCount = orderMapper.selectCount(pendingQuery);

        QueryWrapper<Order> pickupQuery = new QueryWrapper<>();
        pickupQuery.eq("status", 6).eq("rider_id", rider.getId());
        long pickupCount = orderMapper.selectCount(pickupQuery);

        QueryWrapper<Order> deliveringQuery = new QueryWrapper<>();
        deliveringQuery.eq("status", 7).eq("rider_id", rider.getId());
        long deliveringCount = orderMapper.selectCount(deliveringQuery);

        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 4).eq("rider_id", rider.getId()).orderByDesc("datetime");
        List<Order> orderList = orderMapper.selectList(queryWrapper);
        long completedCount = orderList.size();

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

    @RequestMapping("/profile")
    public ModelAndView profile(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        Rider rider = (Rider) request.getSession().getAttribute("rider");
        if (rider == null) {
            modelAndView.setViewName("redirect:/rider/login");
            return modelAndView;
        }

        QueryWrapper<Order> todayQuery = new QueryWrapper<>();
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        todayQuery.eq("status", 4).eq("rider_id", rider.getId()).like("complete_time", today);
        long todayCount = orderMapper.selectCount(todayQuery);

        QueryWrapper<Order> monthQuery = new QueryWrapper<>();
        String month = new SimpleDateFormat("yyyy-MM").format(new Date());
        monthQuery.eq("status", 4).eq("rider_id", rider.getId()).like("complete_time", month);
        long monthCount = orderMapper.selectCount(monthQuery);

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

    @RequestMapping("/notifications")
    public ModelAndView notifications(@RequestParam(defaultValue = "all") String type, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        Rider rider = (Rider) request.getSession().getAttribute("rider");
        if (rider == null) {
            modelAndView.setViewName("redirect:/rider/login");
            return modelAndView;
        }

        List<Notification> notifications = new ArrayList<>();
        Integer unreadCount = 0;

        try {
            if ("all".equals(type)) {
                notifications = notificationMapper.selectByRiderId(rider.getId());
            } else {
                notifications = notificationMapper.selectByRiderIdAndType(rider.getId(), type);
            }

            for (Notification notification : notifications) {
                notification.setTime(formatTime(notification.getCreateTime()));
            }

            unreadCount = notificationMapper.countUnread(rider.getId());
        } catch (Exception e) {
        }

        modelAndView.addObject("rider", rider);
        modelAndView.addObject("notifications", notifications);
        modelAndView.addObject("unreadCount", unreadCount);
        modelAndView.setViewName("rider/riderNotifications");
        return modelAndView;
    }

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

    @RequestMapping("/income")
    public ModelAndView income(@RequestParam(defaultValue = "all") String type, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        Rider rider = (Rider) request.getSession().getAttribute("rider");
        if (rider == null) {
            modelAndView.setViewName("redirect:/rider/login");
            return modelAndView;
        }

        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String month = new SimpleDateFormat("yyyy-MM").format(new Date());
        String lastMonth = new SimpleDateFormat("yyyy-MM").format(new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000));

        QueryWrapper<Order> completedQuery = new QueryWrapper<>();
        completedQuery.eq("status", 4).eq("rider_id", rider.getId());
        
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

    @RequestMapping("/settings")
    public ModelAndView settings(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        Rider rider = (Rider) request.getSession().getAttribute("rider");
        if (rider == null) {
            modelAndView.setViewName("redirect:/rider/login");
            return modelAndView;
        }
        modelAndView.addObject("rider", rider);
        modelAndView.setViewName("rider/riderSettings");
        return modelAndView;
    }

    @RequestMapping("/changePassword")
    public ModelAndView changePassword(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        Rider rider = (Rider) request.getSession().getAttribute("rider");
        if (rider == null) {
            modelAndView.setViewName("redirect:/rider/login");
            return modelAndView;
        }

        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (!rider.getPassword().equals(oldPassword)) {
            modelAndView.addObject("msg", "原密码错误！");
            modelAndView.addObject("rider", rider);
            modelAndView.setViewName("rider/riderSettings");
            return modelAndView;
        }

        if (!newPassword.equals(confirmPassword)) {
            modelAndView.addObject("msg", "两次输入的新密码不一致！");
            modelAndView.addObject("rider", rider);
            modelAndView.setViewName("rider/riderSettings");
            return modelAndView;
        }

        rider.setPassword(newPassword);
        riderService.updateRider(rider);

        modelAndView.addObject("msg", "密码修改成功！");
        modelAndView.addObject("rider", rider);
        modelAndView.setViewName("rider/riderSettings");
        return modelAndView;
    }
}