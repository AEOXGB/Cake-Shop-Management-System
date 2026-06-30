package com.fr.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fr.javaBean.Goods;
import com.fr.javaBean.Notification;
import com.fr.javaBean.Order;
import com.fr.javaBean.OrderItem;
import com.fr.javaBean.Rider;
import com.fr.javaBean.Type;
import com.fr.javaBean.User;
import com.fr.mapper.NotificationMapper;
import com.fr.mapper.OrderItemMapper;
import com.fr.mapper.OrderMapper;
import com.fr.mapper.UserMapper;
import com.fr.service.GoodsService;
import com.fr.service.RiderService;
import com.fr.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.fr.javaBean.OperationLog;
import com.fr.mapper.GoodsMapper;
import com.fr.mapper.OperationLogMapper;

/**
 * 管理员控制器
 * 所属模块：后台管理模块
 * 处理请求路径：/admin
 * 功能描述：负责管理员后台的所有功能，包括系统概览、商品管理、订单管理、用户管理、
 *           骑手管理、数据统计、库存预警、系统设置、操作日志等
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private GoodsMapper goodsMapper;
    
    @Autowired
    private OperationLogMapper operationLogMapper;

    @Autowired
    private RiderService riderService;

    @Autowired
    private NotificationMapper notificationMapper;

    /**
     * 系统概览 - 管理员首页
     * 显示管理员后台首页，包含商品总数、订单总数、用户总数、销售总额、今日订单数、
     * 待发货订单数、分类数量、本月销售额、热销商品、最近订单等统计数据
     * @param request HttpServletRequest请求对象，用于获取session中的用户信息
     * @return ModelAndView 返回管理员首页视图，包含各类统计数据
     */
    @RequestMapping("/index")
    public ModelAndView adminIndex(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        // 检查登录状态
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || !"1".equals(user.getIsadmin())) {
            modelAndView.setViewName("redirect:/login.jsp");
            return modelAndView;
        }
        
        // 统计数据
        long goodsCount = goodsService.countGoods();
        
        // 订单统计
        QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
        long orderCount = orderMapper.selectCount(orderQueryWrapper);
        
        // 用户统计
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        long userCount = userMapper.selectCount(userQueryWrapper);
        
        // 销售总额
        double salesAmount = 0;
        List<Order> orders = orderMapper.selectList(orderQueryWrapper);
        for (Order order : orders) {
            if (order.getTotal() > 0) {
                salesAmount += order.getTotal();
            }
        }
        
        // 今日订单数
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        QueryWrapper<Order> todayOrderQuery = new QueryWrapper<>();
        todayOrderQuery.like("datetime", today);
        long todayOrders = orderMapper.selectCount(todayOrderQuery);
        
        // 待发货订单数
        QueryWrapper<Order> pendingOrderQuery = new QueryWrapper<>();
        pendingOrderQuery.eq("status", 2);
        long pendingOrders = orderMapper.selectCount(pendingOrderQuery);
        
        // 分类数量
        long categoryCount = typeService.countTypes();
        
        // 本月销售额
        String month = new SimpleDateFormat("yyyy-MM").format(new Date());
        QueryWrapper<Order> monthOrderQuery = new QueryWrapper<>();
        monthOrderQuery.like("datetime", month);
        List<Order> monthOrders = orderMapper.selectList(monthOrderQuery);
        double monthSales = 0;
        for (Order order : monthOrders) {
            if (order.getTotal() > 0) {
                monthSales += order.getTotal();
            }
        }
        
        // 热销商品
        List<Map<String, Object>> hotGoods = getHotGoods(5);
        
        // 最近订单
        List<Map<String, Object>> recentOrders = getRecentOrders(5);
        
        modelAndView.addObject("goodsCount", goodsCount);
        modelAndView.addObject("orderCount", orderCount);
        modelAndView.addObject("userCount", userCount);
        modelAndView.addObject("salesAmount", String.format("%.2f", salesAmount));
        modelAndView.addObject("todayOrders", todayOrders);
        modelAndView.addObject("pendingOrders", pendingOrders);
        modelAndView.addObject("categoryCount", categoryCount);
        modelAndView.addObject("monthSales", String.format("%.2f", monthSales));
        modelAndView.addObject("hotGoods", hotGoods);
        modelAndView.addObject("recentOrders", recentOrders);
        modelAndView.addObject("active", 0);
        modelAndView.addObject("pageTitle", "系统概览");
        modelAndView.addObject("content", "admin/adminIndex :: content");
        modelAndView.setViewName("admin/adminLayout");
        return modelAndView;
    }

    /**
     * 获取热销商品列表
     * 根据订单项统计商品销量，返回销量最高的前N个商品
     * @param limit 返回商品数量限制
     * @return List<Map<String, Object>> 热销商品列表，包含商品ID、名称、封面、价格、销量
     */
    private List<Map<String, Object>> getHotGoods(int limit) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        QueryWrapper<OrderItem> itemQuery = new QueryWrapper<>();
        List<OrderItem> items = orderItemMapper.selectList(itemQuery);
        
        Map<Integer, Integer> goodsCountMap = new HashMap<>();
        for (OrderItem item : items) {
            goodsCountMap.put(item.getGoodsId(), goodsCountMap.getOrDefault(item.getGoodsId(), 0) + item.getAmount());
        }
        
        List<Map.Entry<Integer, Integer>> sortedList = new ArrayList<>(goodsCountMap.entrySet());
        sortedList.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        int count = 0;
        for (Map.Entry<Integer, Integer> entry : sortedList) {
            if (count >= limit) break;
            
            QueryWrapper<Goods> goodsQuery = new QueryWrapper<>();
            goodsQuery.eq("id", entry.getKey());
            List<Goods> goodsList = goodsService.findGoods(goodsQuery);
            
            if (!goodsList.isEmpty()) {
                Goods goods = goodsList.get(0);
                Map<String, Object> map = new HashMap<>();
                map.put("id", goods.getId());
                map.put("name", goods.getName());
                map.put("cover", goods.getCover());
                map.put("price", goods.getPrice());
                map.put("sales", entry.getValue());
                result.add(map);
                count++;
            }
        }
        
        return result;
    }

    /**
     * 获取最近订单列表
     * 按时间倒序查询最近的N条订单
     * @param limit 返回订单数量限制
     * @return List<Map<String, Object>> 最近订单列表，包含订单ID、收货人、金额、时间、状态
     */
    private List<Map<String, Object>> getRecentOrders(int limit) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        QueryWrapper<Order> orderQuery = new QueryWrapper<>();
        orderQuery.orderByDesc("datetime").last("LIMIT " + limit);
        List<Order> orders = orderMapper.selectList(orderQuery);
        
        for (Order order : orders) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", order.getId());
            map.put("name", order.getName());
            map.put("total", order.getTotal());
            map.put("datetime", order.getDatetime());
            map.put("status", order.getStatus());
            map.put("statusText", getStatusText(order.getStatus()));
            result.add(map);
        }
        
        return result;
    }

    /**
     * 获取订单状态文本描述
     * 根据订单状态码返回对应的中文状态描述
     * @param status 订单状态码（2-已付款，3-待配送，4-已完成，5-已取消，6-待取货，7-配送中）
     * @return String 订单状态中文描述
     */
    private String getStatusText(int status) {
        switch (status) {
            case 2: return "已付款";
            case 3: return "待配送";
            case 4: return "已完成";
            case 5: return "已取消";
            case 6: return "待取货";
            case 7: return "配送中";
            default: return "未知";
        }
    }

    /**
     * 商品列表页面
     * 显示所有商品列表，支持按商品ID和名称搜索，支持分页功能，同时显示库存预警数量
     * @param request HttpServletRequest请求对象，包含keyword（搜索关键词）、page（当前页码）参数
     * @return ModelAndView 返回商品列表页面视图
     */
    @RequestMapping("/goodsList")
    public ModelAndView goodsList(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || !"1".equals(user.getIsadmin())) {
            modelAndView.setViewName("redirect:/login.jsp");
            return modelAndView;
        }
        
        String keyword = request.getParameter("keyword");
        
        String pageStr = request.getParameter("page");
        int page = pageStr != null ? Integer.parseInt(pageStr) : 1;
        int pageSize = 5;
        
        List<Goods> goodsList = goodsMapper.selectList(null);
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            String searchKeyword = keyword.trim();
            List<Goods> filteredList = new ArrayList<>();
            for (Goods goods : goodsList) {
                boolean match = false;
                if (goods.getName() != null && goods.getName().contains(searchKeyword)) {
                    match = true;
                }
                if (String.valueOf(goods.getId()).contains(searchKeyword)) {
                    match = true;
                }
                if (match) {
                    filteredList.add(goods);
                }
            }
            goodsList = filteredList;
        }
        
        List<Type> typeList = typeService.findTypes(null);
        
        for (Goods goods : goodsList) {
            if (goods.getTypeId() <= 0) {
                goods.setTypeName("未分类");
                continue;
            }
            for (Type type : typeList) {
                if (goods.getTypeId() == type.getId()) {
                    goods.setTypeName(type.getName());
                    break;
                }
            }
            if (goods.getTypeName() == null) {
                goods.setTypeName("未分类");
            }
        }
        
        int totalItems = goodsList.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        
        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;
        
        // 应用分页截取
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalItems);
        List<Goods> pageGoods = totalItems > 0 ? goodsList.subList(startIndex, endIndex) : new ArrayList<>();
        
        // 查询库存预警数量
        HttpSession session = request.getSession();
        Integer warningThreshold = (Integer) session.getAttribute("warningThreshold");
        if (warningThreshold == null) {
            warningThreshold = 40;
        }
        QueryWrapper<Goods> warningQuery = new QueryWrapper<>();
        warningQuery.lt("stock", warningThreshold);
        long warningCountLong = goodsMapper.selectCount(warningQuery);
        int warningCount = (int) warningCountLong;
        
        modelAndView.addObject("goodsList", pageGoods);
        modelAndView.addObject("typeList", typeList);
        modelAndView.addObject("keyword", keyword);
        modelAndView.addObject("totalCount", totalItems);
        modelAndView.addObject("totalPages", totalPages);
        modelAndView.addObject("currentPage", page);
        modelAndView.addObject("warningCount", warningCount);
        modelAndView.addObject("active", 0);
        modelAndView.addObject("pageTitle", "商品列表");
        modelAndView.addObject("content", "admin/adminGoodsList :: content");
        modelAndView.setViewName("admin/adminLayout");
        return modelAndView;
    }

    /**
     * 商品详情页面
     * 显示指定商品的详细信息，包括商品基本信息和分类列表
     * @param request HttpServletRequest请求对象，包含id（商品ID）参数
     * @return ModelAndView 返回商品详情页面视图
     */
    @RequestMapping("/goodsDetail")
    public ModelAndView goodsDetail(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || !"1".equals(user.getIsadmin())) {
            modelAndView.setViewName("redirect:/login.jsp");
            return modelAndView;
        }
        
        String idStr = request.getParameter("id");
        Goods goods = null;
        if (idStr != null && !idStr.isEmpty()) {
            goods = goodsMapper.selectById(Integer.parseInt(idStr));
        }
        
        List<Type> typeList = typeService.findTypes(null);
        
        modelAndView.addObject("goods", goods);
        modelAndView.addObject("typeList", typeList);
        modelAndView.addObject("active", 1);
        modelAndView.addObject("pageTitle", "商品详情");
        modelAndView.addObject("content", "admin/adminGoodsDetail :: content");
        modelAndView.setViewName("admin/adminLayout");
        return modelAndView;
    }

    /**
     * 编辑商品页面
     * 显示商品编辑表单，用于修改已有商品的信息
     * @param request HttpServletRequest请求对象，包含id（商品ID）参数
     * @return ModelAndView 返回商品编辑页面视图
     */
    @RequestMapping("/editGoods")
    public ModelAndView editGoods(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || !"1".equals(user.getIsadmin())) {
            modelAndView.setViewName("redirect:/login.jsp");
            return modelAndView;
        }
        
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            int goodsId = Integer.parseInt(idStr);
            QueryWrapper<Goods> query = new QueryWrapper<>();
            query.eq("id", goodsId);
            List<Goods> goodsList = goodsService.findGoods(query);
            if (!goodsList.isEmpty()) {
                modelAndView.addObject("goods", goodsList.get(0));
            }
        }
        
        List<Type> typeList = typeService.findTypes(null);
        modelAndView.addObject("typeList", typeList);
        modelAndView.addObject("active", 1);
        modelAndView.addObject("pageTitle", "编辑商品");
        modelAndView.addObject("content", "admin/adminAddGoods :: content");
        modelAndView.setViewName("admin/adminLayout");
        return modelAndView;
    }

    /**
     * 删除商品
     * 根据商品ID删除指定商品
     * @param request HttpServletRequest请求对象，包含id（商品ID）参数
     * @return String 重定向到商品列表页面
     */
    @RequestMapping("/deleteGoods")
    public String deleteGoods(HttpServletRequest request) {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            int goodsId = Integer.parseInt(idStr);
            goodsMapper.deleteById(goodsId);
        }
        return "redirect:/admin/goodsList";
    }

    /**
     * 保存商品（新增或更新）
     * 处理商品表单提交，根据是否有ID判断是新增还是更新商品，支持图片上传
     * 新增商品时会向所有已审核的骑手发送新商品上架通知
     * @param request HttpServletRequest请求对象，包含商品信息和图片文件
     * @return String 重定向到商品列表页面
     */
    @RequestMapping("/saveGoods")
    public String saveGoods(HttpServletRequest request) {
        String idStr = request.getParameter("id");
        String name = request.getParameter("name");
        String priceStr = request.getParameter("price");
        String stockStr = request.getParameter("stock");
        String typeIdStr = request.getParameter("type_id");
        String intro = request.getParameter("intro");
        
        if (name != null && !name.trim().isEmpty() && priceStr != null && !priceStr.isEmpty()) {
            Goods goods = new Goods();
            goods.setName(name.trim());
            goods.setPrice(priceStr);  // price是String类型，直接设置
            
            if (stockStr != null && !stockStr.isEmpty()) {
                goods.setStock(Integer.parseInt(stockStr));
            }
            if (typeIdStr != null && !typeIdStr.isEmpty()) {
                goods.setTypeId(Integer.parseInt(typeIdStr));
            }
            if (intro != null) {
                goods.setIntro(intro);
            }
            
            // 设置默认图片
            goods.setCover("/picture/default.jpg");
            goods.setImage1("/picture/default.jpg");
            goods.setImage2("/picture/default.jpg");
            
            // 设置上架时间
            goods.setDaytime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            
            // 处理文件上传
            if (request instanceof org.springframework.web.multipart.MultipartHttpServletRequest) {
                org.springframework.web.multipart.MultipartHttpServletRequest multipartRequest = 
                    (org.springframework.web.multipart.MultipartHttpServletRequest) request;
                
                // 处理主图
                MultipartFile coverFile = multipartRequest.getFile("cover");
                if (coverFile != null && !coverFile.isEmpty()) {
                    String coverPath = saveFile(coverFile, request);
                    if (coverPath != null) {
                        goods.setCover(coverPath);
                    }
                }
                
                // 处理图片1
                MultipartFile image1File = multipartRequest.getFile("image1");
                if (image1File != null && !image1File.isEmpty()) {
                    String image1Path = saveFile(image1File, request);
                    if (image1Path != null) {
                        goods.setImage1(image1Path);
                    }
                }
                
                // 处理图片2
                MultipartFile image2File = multipartRequest.getFile("image2");
                if (image2File != null && !image2File.isEmpty()) {
                    String image2Path = saveFile(image2File, request);
                    if (image2Path != null) {
                        goods.setImage2(image2Path);
                    }
                }
            }
            
            if (idStr != null && !idStr.isEmpty()) {
                // 更新商品
                Goods existingGoods = goodsMapper.selectById(Integer.parseInt(idStr));
                if (existingGoods != null) {
                    // 保留原有的图片路径（如果没有上传新图片）
                    if (goods.getCover().equals("/picture/default.jpg")) {
                        goods.setCover(existingGoods.getCover());
                    }
                    if (goods.getImage1().equals("/picture/default.jpg")) {
                        goods.setImage1(existingGoods.getImage1());
                    }
                    if (goods.getImage2().equals("/picture/default.jpg")) {
                        goods.setImage2(existingGoods.getImage2());
                    }
                }
                goods.setId(Integer.parseInt(idStr));
                goodsMapper.updateById(goods);
            } else {
                // 新增商品
                goodsMapper.insert(goods);
                
                // 向所有骑手发送新商品通知
                sendNewGoodsNotification(goods.getName());
            }
        }
        
        return "redirect:/admin/goodsList";
    }
    
    /**
     * 向所有骑手发送新商品通知
     * 新商品上架时，向所有已审核通过的骑手发送系统通知
     * @param goodsName 新上架商品的名称
     */
    private void sendNewGoodsNotification(String goodsName) {
        try {
            // 获取所有已审核通过的骑手
            List<Rider> riders = riderService.getAllRiders();
            
            for (Rider rider : riders) {
                if (rider.getStatus() == 1) { // 只向已审核的骑手发送通知
                    Notification notification = new Notification();
                    notification.setRiderId(rider.getId());
                    notification.setTitle("新商品上架");
                    notification.setContent("新商品「" + goodsName + "」已上架，快来查看吧！");
                    notification.setType("system");
                    notification.setIsRead(0);
                    notification.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    notificationMapper.insert(notification);
                }
            }
        } catch (Exception e) {
            // 通知发送失败不影响商品添加
        }
    }
    
    // 文件上传计数器，用于生成唯一文件名
    private static int fileCounter = 0;

    /**
     * 保存上传的文件
     * 将上传的文件保存到项目根目录的picture文件夹中，使用时间戳+计数器确保文件名唯一
     * @param file 上传的文件对象
     * @param request HttpServletRequest请求对象
     * @return String 文件保存后的访问路径，失败返回null
     */
    private String saveFile(MultipartFile file, HttpServletRequest request) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        // 使用项目外部的固定目录存储图片，避免重启服务器丢失
        String uploadDir = System.getProperty("user.dir") + File.separator + "picture";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        // 使用时间戳+递增计数器确保文件名唯一
        long timestamp = System.currentTimeMillis();
        int count = fileCounter++;
        String newFilename = "p" + timestamp + "_" + count + extension;
        String filePath = uploadDir + File.separator + newFilename;
        
        try {
            file.transferTo(new File(filePath));
            return "/picture/" + newFilename;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 添加商品页面
     * 显示商品添加表单，用于新增商品
     * @param request HttpServletRequest请求对象，用于验证管理员权限
     * @return ModelAndView 返回商品添加页面视图
     */
    @RequestMapping("/addGoods")
    public ModelAndView addGoods(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || !"1".equals(user.getIsadmin())) {
            modelAndView.setViewName("redirect:/login.jsp");
            return modelAndView;
        }
        
        List<Type> typeList = typeService.findTypes(null);
        modelAndView.addObject("typeList", typeList);
        modelAndView.addObject("active", 1);
        modelAndView.addObject("pageTitle", "添加商品");
        modelAndView.addObject("content", "admin/adminAddGoods :: content");
        modelAndView.setViewName("admin/adminLayout");
        return modelAndView;
    }

    /**
     * 分类管理页面
     * 显示所有商品分类列表，用于分类管理
     * @param request HttpServletRequest请求对象，用于验证管理员权限
     * @return ModelAndView 返回分类管理页面视图
     */
    @RequestMapping("/categoryList")
    public ModelAndView categoryList(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || !"1".equals(user.getIsadmin())) {
            modelAndView.setViewName("redirect:/login.jsp");
            return modelAndView;
        }
        
        List<Type> typeList = typeService.findTypes(null);
        modelAndView.addObject("typeList", typeList);
        modelAndView.addObject("active", 1);
        modelAndView.addObject("pageTitle", "分类管理");
        modelAndView.addObject("content", "admin/adminCategoryList :: content");
        modelAndView.setViewName("admin/adminLayout");
        return modelAndView;
    }

    /**
     * 订单列表页面
     * 显示所有订单列表，支持按订单ID和收货人姓名搜索，支持分页
     * @param request HttpServletRequest请求对象，包含keyword（搜索关键词）、page（当前页码）参数
     * @return ModelAndView 返回订单列表页面视图
     */
    @RequestMapping("/orderList")
    public ModelAndView orderList(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || !"1".equals(user.getIsadmin())) {
            modelAndView.setViewName("redirect:/login.jsp");
            return modelAndView;
        }
        
        String keyword = request.getParameter("keyword");
        
        String pageStr = request.getParameter("page");
        int page = pageStr != null ? Integer.parseInt(pageStr) : 1;
        int pageSize = 5;
        
        QueryWrapper<Order> orderQuery = new QueryWrapper<>();
        orderQuery.orderByDesc("datetime");
        
        if (keyword != null && !keyword.isEmpty()) {
            orderQuery.and(q -> q.like("id", keyword).or().like("name", keyword));
        }
        
        List<Order> orderList = orderMapper.selectList(orderQuery);
        List<Map<String, Object>> orders = new ArrayList<>();
        
        for (Order order : orderList) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", order.getId());
            map.put("name", order.getName());
            map.put("total", order.getTotal());
            map.put("datetime", order.getDatetime());
            map.put("status", order.getStatus());
            map.put("statusText", getStatusText(order.getStatus()));
            map.put("phone", order.getPhone());
            map.put("address", order.getAddress());
            orders.add(map);
        }
        
        int totalItems = orders.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        
        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;
        
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalItems);
        List<Map<String, Object>> pageOrders = new ArrayList<>();
        if (totalItems > 0) {
            pageOrders.addAll(orders.subList(startIndex, endIndex));
        }
        
        modelAndView.addObject("orderList", pageOrders);
        modelAndView.addObject("keyword", keyword);
        modelAndView.addObject("totalItems", totalItems);
        modelAndView.addObject("totalPages", totalPages);
        modelAndView.addObject("currentPage", page);
        modelAndView.addObject("active", 2);
        modelAndView.addObject("pageTitle", "订单列表");
        modelAndView.addObject("content", "admin/adminOrderList :: content");
        modelAndView.setViewName("admin/adminLayout");
        return modelAndView;
    }

    /**
     * 订单详情页面
     * 显示指定订单的详细信息，包括订单基本信息和订单项列表
     * @param request HttpServletRequest请求对象，包含id（订单ID）参数
     * @return ModelAndView 返回订单详情页面视图
     */
    @RequestMapping("/orderDetail")
    public ModelAndView orderDetail(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || !"1".equals(user.getIsadmin())) {
            modelAndView.setViewName("redirect:/login.jsp");
            return modelAndView;
        }
        
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            long orderId = Long.parseLong(idStr);
            Order order = orderMapper.selectById(orderId);
            
            if (order != null) {
                modelAndView.addObject("order", order);
                modelAndView.addObject("statusText", getStatusText(order.getStatus()));
                
                QueryWrapper<OrderItem> itemQuery = new QueryWrapper<>();
                itemQuery.eq("order_id", orderId);
                List<OrderItem> items = orderItemMapper.selectList(itemQuery);
                
                List<Map<String, Object>> orderItems = new ArrayList<>();
                for (OrderItem item : items) {
                    Map<String, Object> map = new HashMap<>();
                    QueryWrapper<Goods> goodsQuery = new QueryWrapper<>();
                    goodsQuery.eq("id", item.getGoodsId());
                    List<Goods> goodsList = goodsService.findGoods(goodsQuery);
                    
                    if (!goodsList.isEmpty()) {
                        Goods goods = goodsList.get(0);
                        map.put("goodsName", goods.getName());
                        map.put("goodsCover", goods.getCover());
                        try {
                            Double price = Double.parseDouble(goods.getPrice());
                            map.put("goodsPrice", price);
                        } catch (NumberFormatException e) {
                            map.put("goodsPrice", 0.0);
                        }
                    } else {
                        map.put("goodsName", "商品已删除");
                        map.put("goodsCover", "");
                        map.put("goodsPrice", 0.0);
                    }
                    map.put("amount", item.getAmount());
                    Double price = (Double) map.get("goodsPrice");
                    map.put("totalPrice", item.getAmount() * price);
                    orderItems.add(map);
                }
                modelAndView.addObject("orderItems", orderItems);
            }
        }
        
        modelAndView.addObject("active", 2);
        modelAndView.addObject("pageTitle", "订单详情");
        modelAndView.addObject("content", "admin/adminOrderDetail :: content");
        modelAndView.setViewName("admin/adminLayout");
        return modelAndView;
    }

    /**
     * 订单发货
     * 将已付款（状态2）的订单状态更新为待配送（状态3）
     * @param request HttpServletRequest请求对象，包含id（订单ID）参数
     * @return String 重定向到订单列表页面
     */
    @RequestMapping("/shipOrder")
    public String shipOrder(HttpServletRequest request) {
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            long orderId = Long.parseLong(idStr);
            Order order = orderMapper.selectById(orderId);
            
            if (order != null && order.getStatus() == 2) {
                order.setStatus(3);
                orderMapper.updateById(order);
            }
        }
        return "redirect:/admin/orderList";
    }

    /**
     * 设置订单佣金
     * 显示设置佣金页面并处理佣金设置请求，为订单设置配送佣金金额
     * @param request HttpServletRequest请求对象，包含id（订单ID）、commission（佣金金额）参数
     * @return ModelAndView 返回佣金设置页面视图
     */
    @RequestMapping("/setCommission")
    public ModelAndView setCommission(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || !"1".equals(user.getIsadmin())) {
            modelAndView.setViewName("redirect:/login.jsp");
            return modelAndView;
        }
        
        String idStr = request.getParameter("id");
        String commissionStr = request.getParameter("commission");
        
        if (idStr != null && !idStr.isEmpty()) {
            long orderId = Long.parseLong(idStr);
            Order order = orderMapper.selectById(orderId);
            
            if (order != null) {
                if (commissionStr != null && !commissionStr.isEmpty()) {
                    try {
                        double commission = Double.parseDouble(commissionStr);
                        order.setCommission(commission);
                        orderMapper.updateById(order);
                        modelAndView.addObject("msg", "佣金设置成功！");
                    } catch (NumberFormatException e) {
                        modelAndView.addObject("msg", "佣金格式错误！");
                    }
                }
                modelAndView.addObject("order", order);
            }
        }
        
        modelAndView.addObject("active", 2);
        modelAndView.addObject("pageTitle", "设置佣金");
        modelAndView.addObject("content", "admin/adminSetCommission :: content");
        modelAndView.setViewName("admin/adminLayout");
        return modelAndView;
    }

    /**
     * 销售统计页面
     * 显示销售统计数据，包括活跃用户数、今日注册用户数、各状态订单数、
     * 本月订单数、本月销售额、商品总数等
     * @param request HttpServletRequest请求对象，用于验证管理员权限
     * @return ModelAndView 返回销售统计页面视图
     */
    @RequestMapping("/orderStats")
    public ModelAndView orderStats(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || !"1".equals(user.getIsadmin())) {
            modelAndView.setViewName("redirect:/login.jsp");
            return modelAndView;
        }
        
        QueryWrapper<User> activeUserQuery = new QueryWrapper<>();
        activeUserQuery.eq("isvalidate", "1");
        long activeUserCount = userMapper.selectCount(activeUserQuery);
        
        // 查询当天注册用户数
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
        QueryWrapper<User> todayUserQuery = new QueryWrapper<>();
        todayUserQuery.likeRight("createtime", today);
        long todayRegisterCount = userMapper.selectCount(todayUserQuery);
        
        // 查询订单状态统计
        QueryWrapper<Order> unpaidQuery = new QueryWrapper<>();
        unpaidQuery.eq("status", 1);
        long unpaidCount = orderMapper.selectCount(unpaidQuery);
        
        QueryWrapper<Order> paidQuery = new QueryWrapper<>();
        paidQuery.eq("status", 2);
        long paidCount = orderMapper.selectCount(paidQuery);
        
        QueryWrapper<Order> deliveredQuery = new QueryWrapper<>();
        deliveredQuery.eq("status", 4);
        long deliveredCount = orderMapper.selectCount(deliveredQuery);
        
        // 查询本月订单数和销售额
        String month = new java.text.SimpleDateFormat("yyyy-MM").format(new java.util.Date());
        QueryWrapper<Order> monthOrderQuery = new QueryWrapper<>();
        monthOrderQuery.likeRight("createtime", month);
        List<Order> monthOrders = orderMapper.selectList(monthOrderQuery);
        long monthOrderCount = monthOrders.size();
        double monthSalesAmount = 0;
        for (Order order : monthOrders) {
            if (order.getStatus() == 2 || order.getStatus() == 3 || order.getStatus() == 4) {
                monthSalesAmount += order.getTotal();
            }
        }
        
        // 查询商品总数
        long goodsCount = goodsMapper.selectCount(null);
        
        modelAndView.addObject("activeUserCount", activeUserCount);
        modelAndView.addObject("todayRegisterCount", todayRegisterCount);
        modelAndView.addObject("unpaidCount", unpaidCount);
        modelAndView.addObject("paidCount", paidCount);
        modelAndView.addObject("deliveredCount", deliveredCount);
        modelAndView.addObject("monthOrderCount", monthOrderCount);
        modelAndView.addObject("monthSalesAmount", monthSalesAmount);
        modelAndView.addObject("goodsCount", goodsCount);
        modelAndView.addObject("active", 3);
        modelAndView.addObject("pageTitle", "销售统计");
        modelAndView.addObject("content", "admin/adminSalesStats :: content");
        modelAndView.setViewName("admin/adminLayout");
        return modelAndView;
    }

    /**
     * 未审核用户列表页面
     * 显示待审核和已冻结的用户列表，支持搜索和分页功能
     * @param request HttpServletRequest请求对象，包含keyword（搜索关键词）、page（当前页码）参数
     * @return ModelAndView 返回未审核用户列表页面视图
     */
    @RequestMapping("/userList")
    public ModelAndView userList(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || !"1".equals(user.getIsadmin())) {
            modelAndView.setViewName("redirect:/login.jsp");
            return modelAndView;
        }
        
        String keyword = request.getParameter("keyword");
        String pageStr = request.getParameter("page");
        int page = pageStr != null ? Integer.parseInt(pageStr) : 1;
        int pageSize = 5;
        
        QueryWrapper<User> userQuery = new QueryWrapper<>();
        userQuery.in("isvalidate", "0", "2");
        userQuery.orderByDesc("id");
        
        List<User> userList = userMapper.selectList(userQuery);
        
        // 搜索过滤
        if (keyword != null && !keyword.trim().isEmpty()) {
            String searchKeyword = keyword.trim();
            List<User> filteredList = new ArrayList<>();
            for (User u : userList) {
                boolean match = false;
                if (u.getUsername() != null && u.getUsername().contains(searchKeyword)) match = true;
                if (u.getName() != null && u.getName().contains(searchKeyword)) match = true;
                if (u.getPhone() != null && u.getPhone().contains(searchKeyword)) match = true;
                if (u.getEmail() != null && u.getEmail().contains(searchKeyword)) match = true;
                if (match) {
                    filteredList.add(u);
                }
            }
            userList = filteredList;
        }
        
        int totalItems = userList.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalItems);
        List<User> pageUsers = totalItems > 0 ? userList.subList(startIndex, endIndex) : new java.util.ArrayList<>();
        
        modelAndView.addObject("userList", pageUsers);
        modelAndView.addObject("totalItems", totalItems);
        modelAndView.addObject("totalPages", totalPages);
        modelAndView.addObject("currentPage", page);
        modelAndView.addObject("keyword", keyword);
        modelAndView.addObject("active", 4);
        modelAndView.addObject("pageTitle", "未审核用户");
        modelAndView.addObject("content", "admin/adminUserList :: content");
        modelAndView.setViewName("admin/adminLayout");
        return modelAndView;
    }
    
    /**
     * 审核通过用户
     * 将用户状态设置为已审核通过（isvalidate=1）
     * @param request HttpServletRequest请求对象，包含userId（用户ID）参数
     * @return ModelAndView 重定向到未审核用户列表页面
     */
    @RequestMapping("/validateUser")
    public ModelAndView validateUser(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        User admin = (User) request.getSession().getAttribute("user");
        if (admin == null || !"1".equals(admin.getIsadmin())) {
            modelAndView.setViewName("redirect:/login.jsp");
            return modelAndView;
        }
        
        String userId = request.getParameter("userId");
        if (userId != null && !userId.isEmpty()) {
            User user = userMapper.selectById(Integer.parseInt(userId));
            if (user != null) {
                user.setIsvalidate("1");
                userMapper.updateById(user);
            }
        }
        
        modelAndView.setViewName("redirect:/admin/userList");
        return modelAndView;
    }
    
    /**
     * 冻结用户
     * 将用户状态设置为已冻结（isvalidate=2）
     * @param request HttpServletRequest请求对象，包含userId（用户ID）参数
     * @return ModelAndView 重定向到未审核用户列表页面
     */
    @RequestMapping("/freezeUser")
    public ModelAndView freezeUser(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        User admin = (User) request.getSession().getAttribute("user");
        if (admin == null || !"1".equals(admin.getIsadmin())) {
            modelAndView.setViewName("redirect:/login.jsp");
            return modelAndView;
        }
        
        String userId = request.getParameter("userId");
        if (userId != null && !userId.isEmpty()) {
            User user = userMapper.selectById(Integer.parseInt(userId));
            if (user != null) {
                user.setIsvalidate("2");
                userMapper.updateById(user);
            }
        }
        
        modelAndView.setViewName("redirect:/admin/userList");
        return modelAndView;
    }
    
    /**
     * 解冻用户
     * 将已冻结用户的状态恢复为待审核（isvalidate=0）
     * @param request HttpServletRequest请求对象，包含userId（用户ID）参数
     * @return ModelAndView 重定向到未审核用户列表页面
     */
    @RequestMapping("/unfreezeUser")
    public ModelAndView unfreezeUser(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        User admin = (User) request.getSession().getAttribute("user");
        if (admin == null || !"1".equals(admin.getIsadmin())) {
            modelAndView.setViewName("redirect:/login.jsp");
            return modelAndView;
        }
        
        String userId = request.getParameter("userId");
        if (userId != null && !userId.isEmpty()) {
            User user = userMapper.selectById(Integer.parseInt(userId));
            if (user != null) {
                user.setIsvalidate("0");
                userMapper.updateById(user);
            }
        }
        
        modelAndView.setViewName("redirect:/admin/userList");
        return modelAndView;
    }
    
    /**
     * 编辑用户信息页面
     * 显示用户信息编辑表单，用于修改用户的基本信息
     * @param request HttpServletRequest请求对象，包含userId（用户ID）参数
     * @return ModelAndView 返回用户信息编辑页面视图
     */
    @RequestMapping("/editUser")
    public ModelAndView editUser(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        User admin = (User) request.getSession().getAttribute("user");
        if (admin == null || !"1".equals(admin.getIsadmin())) {
            modelAndView.setViewName("redirect:/login.jsp");
            return modelAndView;
        }
        
        String userId = request.getParameter("userId");
        if (userId != null && !userId.isEmpty()) {
            User user = userMapper.selectById(Integer.parseInt(userId));
            modelAndView.addObject("user", user);
        }
        
        modelAndView.addObject("active", 4);
        modelAndView.addObject("pageTitle", "修改用户信息");
        modelAndView.addObject("content", "admin/adminEditUser :: content");
        modelAndView.setViewName("admin/adminLayout");
        return modelAndView;
    }
    
    /**
     * 权限管理页面
     * 显示用户权限设置页面，用于设置用户是否为管理员
     * @param request HttpServletRequest请求对象，包含userId（用户ID）参数
     * @return ModelAndView 返回权限管理页面视图
     */
    @RequestMapping("/setAdminPage")
    public ModelAndView setAdminPage(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        User admin = (User) request.getSession().getAttribute("user");
        if (admin == null || !"1".equals(admin.getIsadmin())) {
            modelAndView.setViewName("redirect:/login.jsp");
            return modelAndView;
        }
        
        String userId = request.getParameter("userId");
        if (userId != null && !userId.isEmpty()) {
            User user = userMapper.selectById(Integer.parseInt(userId));
            modelAndView.addObject("user", user);
        }
        
        modelAndView.addObject("active", 4);
        modelAndView.addObject("pageTitle", "权限管理");
        modelAndView.addObject("content", "admin/adminSetAdmin :: content");
        modelAndView.setViewName("admin/adminLayout");
        return modelAndView;
    }
    
    /**
     * 更新用户信息
     * 处理用户信息修改表单提交，更新用户的邮箱、姓名、电话、地址、密码等信息
     * @param request HttpServletRequest请求对象，包含用户信息参数
     * @return ModelAndView 重定向到未审核用户列表页面
     */
    @RequestMapping("/updateUser")
    public ModelAndView updateUser(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        User admin = (User) request.getSession().getAttribute("user");
        if (admin == null || !"1".equals(admin.getIsadmin())) {
            modelAndView.setViewName("redirect:/login.jsp");
            return modelAndView;
        }
        
        String userId = request.getParameter("userId");
        String email = request.getParameter("email");
        String name = request.getParameter("name");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String password = request.getParameter("password");
        
        if (userId != null && !userId.isEmpty()) {
            User user = userMapper.selectById(Integer.parseInt(userId));
            if (user != null) {
                if (email != null && !email.isEmpty()) {
                    user.setEmail(email);
                }
                if (name != null && !name.isEmpty()) {
                    user.setName(name);
                }
                if (phone != null) {
                    user.setPhone(phone);
                }
                if (address != null) {
                    user.setAddress(address);
                }
                if (password != null && !password.isEmpty()) {
                    user.setPassword(password);
                }
                userMapper.updateById(user);
            }
        }
        
        modelAndView.setViewName("redirect:/admin/userList");
        return modelAndView;
    }
    
    /**
     * 设置管理员权限
     * 设置或取消用户的管理员权限
     * @param request HttpServletRequest请求对象，包含userId（用户ID）、isadmin（是否管理员）参数
     * @return ModelAndView 重定向到未审核用户列表页面
     */
    @RequestMapping("/setAdmin")
    public ModelAndView setAdmin(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        User admin = (User) request.getSession().getAttribute("user");
        if (admin == null || !"1".equals(admin.getIsadmin())) {
            modelAndView.setViewName("redirect:/login.jsp");
            return modelAndView;
        }
        
        String userId = request.getParameter("userId");
        String isadmin = request.getParameter("isadmin");
        
        if (userId != null && !userId.isEmpty() && isadmin != null) {
            User user = userMapper.selectById(Integer.parseInt(userId));
            if (user != null) {
                user.setIsadmin(isadmin);
                userMapper.updateById(user);
            }
        }
        
        modelAndView.setViewName("redirect:/admin/userList");
        return modelAndView;
    }

    /**
     * 骑手审核列表页面
     * 显示待审核的骑手列表，支持搜索功能
     * @param request HttpServletRequest请求对象，包含keyword（搜索关键词）参数
     * @return ModelAndView 返回骑手审核列表页面视图
     */
    @RequestMapping("/riderList")
    public ModelAndView riderList(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || !"1".equals(user.getIsadmin())) {
            modelAndView.setViewName("redirect:/login.jsp");
            return modelAndView;
        }
        
        String keyword = request.getParameter("keyword");
        
        List<Rider> riderList = riderService.getAllRiders();
        
        List<Rider> pendingRiders = new ArrayList<>();
        for (Rider rider : riderList) {
            if (rider.getStatus() == 0) {
                pendingRiders.add(rider);
            }
        }
        
        // 搜索过滤
        if (keyword != null && !keyword.trim().isEmpty()) {
            String searchKeyword = keyword.trim();
            List<Rider> filteredList = new ArrayList<>();
            for (Rider rider : pendingRiders) {
                boolean match = false;
                if (rider.getName() != null && rider.getName().contains(searchKeyword)) match = true;
                if (rider.getPhone() != null && rider.getPhone().contains(searchKeyword)) match = true;
                if (match) {
                    filteredList.add(rider);
                }
            }
            pendingRiders = filteredList;
        }
        
        int totalItems = pendingRiders.size();
        
        modelAndView.addObject("riderList", pendingRiders);
        modelAndView.addObject("totalItems", totalItems);
        modelAndView.addObject("totalPages", 1);
        modelAndView.addObject("currentPage", 1);
        modelAndView.addObject("keyword", keyword);
        modelAndView.addObject("active", 6);
        modelAndView.addObject("pageTitle", "骑手审核");
        modelAndView.addObject("content", "admin/adminRiderList :: content");
        modelAndView.setViewName("admin/adminLayout");
        return modelAndView;
    }

    /**
     * 审核通过骑手
     * 将待审核骑手的状态设置为已审核通过（status=1）
     * @param request HttpServletRequest请求对象，包含id（骑手ID）参数
     * @return ModelAndView 重定向到骑手审核列表页面
     */
    @RequestMapping("/approveRider")
    public ModelAndView approveRider(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || !"1".equals(user.getIsadmin())) {
            modelAndView.setViewName("redirect:/login.jsp");
            return modelAndView;
        }
        
        String riderId = request.getParameter("id");
        if (riderId != null && !riderId.isEmpty()) {
            Rider rider = riderService.getRiderById(Integer.parseInt(riderId));
            if (rider != null && rider.getStatus() == 0) {
                rider.setStatus(1);
                rider.setUpdateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                riderService.updateRider(rider);
            }
        }
        
        modelAndView.setViewName("redirect:/admin/riderList");
        return modelAndView;
    }

    /**
     * 拒绝骑手申请
     * 删除待审核的骑手申请记录
     * @param request HttpServletRequest请求对象，包含id（骑手ID）参数
     * @return ModelAndView 重定向到骑手审核列表页面
     */
    @RequestMapping("/rejectRider")
    public ModelAndView rejectRider(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || !"1".equals(user.getIsadmin())) {
            modelAndView.setViewName("redirect:/login.jsp");
            return modelAndView;
        }
        
        String riderId = request.getParameter("id");
        if (riderId != null && !riderId.isEmpty()) {
            riderService.deleteRider(Integer.parseInt(riderId));
        }
        
        modelAndView.setViewName("redirect:/admin/riderList");
        return modelAndView;
    }

    /**
     * 用户列表页面
     * 显示所有已审核通过的用户和骑手列表，支持搜索和分页功能
     * @param request HttpServletRequest请求对象，包含keyword（搜索关键词）、page（当前页码）参数
     * @return ModelAndView 返回用户列表页面视图
     */
    @RequestMapping("/userStats")
    public ModelAndView userStats(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || !"1".equals(user.getIsadmin())) {
            modelAndView.setViewName("redirect:/login.jsp");
            return modelAndView;
        }
        
        String keyword = request.getParameter("keyword");
        String pageStr = request.getParameter("page");
        int page = pageStr != null ? Integer.parseInt(pageStr) : 1;
        int pageSize = 5;
        
        List<User> userList = userMapper.selectList(null);
        
        List<User> validUsers = new ArrayList<>();
        for (User u : userList) {
            if ("1".equals(u.getIsvalidate())) {
                validUsers.add(u);
            }
        }
        
        List<Rider> riderList = riderService.getAllRiders();
        for (Rider rider : riderList) {
            if (rider.getStatus() == 1) {
                User riderUser = new User();
                riderUser.setId(rider.getId());
                riderUser.setUsername(rider.getName());
                riderUser.setEmail("");
                riderUser.setName(rider.getName());
                riderUser.setPhone(rider.getPhone());
                riderUser.setAddress("");
                riderUser.setIsadmin("2");
                riderUser.setIsvalidate("1");
                validUsers.add(riderUser);
            }
        }
        
        // 搜索过滤
        if (keyword != null && !keyword.trim().isEmpty()) {
            String searchKeyword = keyword.trim();
            List<User> filteredList = new ArrayList<>();
            for (User u : validUsers) {
                boolean match = false;
                if (u.getUsername() != null && u.getUsername().contains(searchKeyword)) match = true;
                if (u.getName() != null && u.getName().contains(searchKeyword)) match = true;
                if (u.getPhone() != null && u.getPhone().contains(searchKeyword)) match = true;
                if (match) {
                    filteredList.add(u);
                }
            }
            validUsers = filteredList;
        }
        
        validUsers.sort((a, b) -> Integer.compare(b.getId(), a.getId()));
        
        int totalItems = validUsers.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalItems);
        List<User> pageUsers = totalItems > 0 ? validUsers.subList(startIndex, endIndex) : new java.util.ArrayList<>();
        
        modelAndView.addObject("userList", pageUsers);
        modelAndView.addObject("totalItems", totalItems);
        modelAndView.addObject("totalPages", totalPages);
        modelAndView.addObject("currentPage", page);
        modelAndView.addObject("keyword", keyword);
        modelAndView.addObject("active", 5);
        modelAndView.addObject("pageTitle", "用户列表");
        modelAndView.addObject("content", "admin/adminUserStats :: content");
        modelAndView.setViewName("admin/adminLayout");
        return modelAndView;
    }

    /**
     * 系统设置重定向
     * 重定向到管理员设置页面
     * @param request HttpServletRequest请求对象，用于验证管理员权限
     * @return String 重定向到管理员设置页面
     */
    @RequestMapping("/settings")
    public String settings(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || !"1".equals(user.getIsadmin())) {
            return "redirect:/login.jsp";
        }
        return "redirect:/admin/adminSettings";
    }

    /**
     * 管理员设置页面
     * 显示管理员设置页面，包含库存预警阈值设置、修改密码、操作日志查看入口等
     * @param request HttpServletRequest请求对象，用于验证管理员权限和获取session信息
     * @return ModelAndView 返回管理员设置页面视图
     */
    @RequestMapping("/adminSettings")
    public ModelAndView adminSettings(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || !"1".equals(user.getIsadmin())) {
            modelAndView.setViewName("redirect:/login.jsp");
            return modelAndView;
        }
        
        // 设置默认预警阈值
        HttpSession session = request.getSession();
        if (session.getAttribute("warningThreshold") == null) {
            session.setAttribute("warningThreshold", 40);
        }
        
        // 查询操作日志总数
        long logCount = operationLogMapper.selectCount(null);
        
        // 读取 session 中的密码修改提示并清除
        Boolean passwordChanged = (Boolean) request.getSession().getAttribute("passwordChanged");
        if (passwordChanged != null && passwordChanged) {
            modelAndView.addObject("passwordChanged", true);
            request.getSession().removeAttribute("passwordChanged");
        }
        
        modelAndView.addObject("logCount", logCount);
        modelAndView.addObject("active", 6);
        modelAndView.addObject("pageTitle", "管理员设置");
        modelAndView.addObject("content", "admin/adminSettings :: content");
        modelAndView.setViewName("admin/adminLayout");
        return modelAndView;
    }
    
    /**
     * 操作日志页面
     * 显示所有用户的操作日志，支持按用户名、操作、IP、方法、日志ID搜索，支持分页
     * @param request HttpServletRequest请求对象，包含keyword（搜索关键词）、page（当前页码）参数
     * @return ModelAndView 返回操作日志页面视图
     */
    @RequestMapping("/operationLogs")
    public ModelAndView operationLogs(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || !"1".equals(user.getIsadmin())) {
            modelAndView.setViewName("redirect:/login.jsp");
            return modelAndView;
        }
        
        String keyword = request.getParameter("keyword");
        String pageStr = request.getParameter("page");
        int page = pageStr != null ? Integer.parseInt(pageStr) : 1;
        int pageSize = 5;
        
        // 查询日志（带搜索功能）
        List<OperationLog> allLogs;
        if (keyword != null && !keyword.trim().isEmpty()) {
            String searchKeyword = keyword.trim();
            List<OperationLog> tempList = operationLogMapper.selectList(null);
            List<OperationLog> filteredList = new ArrayList<>();
            for (OperationLog log : tempList) {
                boolean match = false;
                if (log.getUsername() != null && log.getUsername().contains(searchKeyword)) {
                    match = true;
                }
                if (log.getOperation() != null && log.getOperation().contains(searchKeyword)) {
                    match = true;
                }
                if (log.getIp() != null && log.getIp().contains(searchKeyword)) {
                    match = true;
                }
                if (log.getMethod() != null && log.getMethod().contains(searchKeyword)) {
                    match = true;
                }
                if (String.valueOf(log.getId()).contains(searchKeyword)) {
                    match = true;
                }
                if (match) {
                    filteredList.add(log);
                }
            }
            // 按时间降序排序
            filteredList.sort((a, b) -> {
                if (a.getCreateTime() == null) return 1;
                if (b.getCreateTime() == null) return -1;
                return b.getCreateTime().compareTo(a.getCreateTime());
            });
            allLogs = filteredList;
        } else {
            // 按时间降序查询
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<OperationLog> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            queryWrapper.orderByDesc("create_time");
            allLogs = operationLogMapper.selectList(queryWrapper);
        }
        
        int totalItems = allLogs.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;
        
        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalItems);
        List<OperationLog> pageLogs = totalItems > 0 ? allLogs.subList(startIndex, endIndex) : new ArrayList<>();
        
        modelAndView.addObject("logList", pageLogs);
        modelAndView.addObject("keyword", keyword);
        modelAndView.addObject("totalItems", totalItems);
        modelAndView.addObject("totalPages", totalPages);
        modelAndView.addObject("currentPage", page);
        modelAndView.addObject("active", 8);
        modelAndView.addObject("pageTitle", "操作日志");
        modelAndView.addObject("content", "admin/operationLogs :: content");
        modelAndView.setViewName("admin/adminLayout");
        return modelAndView;
    }
    
    /**
     * 库存预警页面
     * 显示库存低于预警阈值的商品列表，支持设置和调整预警阈值
     * @param request HttpServletRequest请求对象，包含action（操作类型）、threshold（阈值）参数
     * @return ModelAndView 返回库存预警页面视图
     */
    @RequestMapping("/stockWarning")
    public ModelAndView stockWarning(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || !"1".equals(user.getIsadmin())) {
            modelAndView.setViewName("redirect:/login.jsp");
            return modelAndView;
        }
        
        HttpSession session = request.getSession();
        
        // 获取或设置默认阈值
        Integer warningThreshold = (Integer) session.getAttribute("warningThreshold");
        if (warningThreshold == null) {
            warningThreshold = 40;
            session.setAttribute("warningThreshold", warningThreshold);
        }
        
        // 处理设置阈值操作
        String action = request.getParameter("action");
        if ("setThreshold".equals(action)) {
            String thresholdStr = request.getParameter("threshold");
            if (thresholdStr != null && !thresholdStr.trim().isEmpty()) {
                try {
                    int newThreshold = Integer.parseInt(thresholdStr.trim());
                    if (newThreshold > 0) {
                        warningThreshold = newThreshold;
                        session.setAttribute("warningThreshold", warningThreshold);
                    }
                } catch (NumberFormatException e) {
                    // 忽略无效输入
                }
            }
        }
        
        // 获取查询阈值（同时保存到session）
        String queryThresholdStr = request.getParameter("threshold");
        int queryThreshold = warningThreshold;
        if (queryThresholdStr != null && !queryThresholdStr.trim().isEmpty()) {
            try {
                queryThreshold = Integer.parseInt(queryThresholdStr.trim());
                if (queryThreshold > 0) {
                    warningThreshold = queryThreshold;
                    session.setAttribute("warningThreshold", warningThreshold);
                }
            } catch (NumberFormatException e) {
                // 使用默认阈值
            }
        }
        
        // 查询库存低于阈值的商品
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        queryWrapper.lt("stock", queryThreshold);
        queryWrapper.orderByDesc("stock");
        List<Goods> warningGoodsList = goodsService.findGoods(queryWrapper);
        
        // 获取分类名称
        Map<Integer, String> categoryMap = new HashMap<>();
        QueryWrapper<Type> typeQuery = new QueryWrapper<>();
        List<Type> categories = typeService.findTypes(typeQuery);
        for (Type category : categories) {
            categoryMap.put(category.getId(), category.getName());
        }
        for (Goods goods : warningGoodsList) {
            goods.setCategoryName(categoryMap.get(goods.getTypeId()));
        }
        
        modelAndView.addObject("warningGoodsList", warningGoodsList);
        modelAndView.addObject("warningThreshold", warningThreshold);
        modelAndView.addObject("warningCount", warningGoodsList.size());
        modelAndView.addObject("active", 7);
        modelAndView.addObject("pageTitle", "库存预警");
        modelAndView.addObject("content", "admin/stockWarning :: content");
        modelAndView.setViewName("admin/adminLayout");
        return modelAndView;
    }
    
    /**
     * 获取库存预警商品数量
     * 查询库存低于预警阈值的商品数量，用于登录时提示
     * @param request HttpServletRequest请求对象，用于获取session中的预警阈值
     * @return int 库存预警商品数量
     */
    public int getWarningGoodsCount(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer warningThreshold = (Integer) session.getAttribute("warningThreshold");
        if (warningThreshold == null) {
            warningThreshold = 40;
        }
        
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        queryWrapper.lt("stock", warningThreshold);
        List<Goods> warningGoodsList = goodsService.findGoods(queryWrapper);
        return warningGoodsList.size();
    }
    
    /**
     * 获取库存预警商品列表
     * 查询库存低于预警阈值的商品列表，用于提示详情展示
     * @param request HttpServletRequest请求对象，用于获取session中的预警阈值
     * @return List<Goods> 库存预警商品列表
     */
    public List<Goods> getWarningGoodsList(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Integer warningThreshold = (Integer) session.getAttribute("warningThreshold");
        if (warningThreshold == null) {
            warningThreshold = 40;
        }
        
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        queryWrapper.lt("stock", warningThreshold);
        queryWrapper.orderByDesc("stock");
        List<Goods> warningGoodsList = goodsService.findGoods(queryWrapper);
        
        // 获取分类名称
        Map<Integer, String> categoryMap = new HashMap<>();
        QueryWrapper<Type> typeQuery = new QueryWrapper<>();
        List<Type> categories = typeService.findTypes(typeQuery);
        for (Type category : categories) {
            categoryMap.put(category.getId(), category.getName());
        }
        for (Goods goods : warningGoodsList) {
            goods.setCategoryName(categoryMap.get(goods.getTypeId()));
        }
        
        return warningGoodsList;
    }
    
    /**
     * 修改管理员密码
     * 验证旧密码正确，且新密码与确认密码一致后，修改当前登录管理员的密码
     * @param request HttpServletRequest请求对象，包含oldPassword（旧密码）、newPassword（新密码）、confirmPassword（确认密码）参数
     * @return String 重定向到管理员设置页面
     */
    @RequestMapping("/changePassword")
    public String changePassword(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || !"1".equals(user.getIsadmin())) {
            return "redirect:/login.jsp";
        }
        
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        if (oldPassword != null && newPassword != null && confirmPassword != null
                && oldPassword.equals(user.getPassword())
                && newPassword.equals(confirmPassword)
                && !newPassword.isEmpty()) {
            user.setPassword(newPassword);
            userMapper.updateById(user);
            request.getSession().setAttribute("passwordChanged", true);
        }
        
        return "redirect:/admin/adminSettings";
    }
}
