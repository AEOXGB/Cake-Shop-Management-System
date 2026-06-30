package com.fr.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fr.javaBean.Cart;
import com.fr.javaBean.Goods;
import com.fr.javaBean.Notification;
import com.fr.javaBean.Order;
import com.fr.javaBean.OrderItem;
import com.fr.javaBean.Rider;
import com.fr.javaBean.User;
import com.fr.mapper.NotificationMapper;
import com.fr.mapper.OrderItemMapper;
import com.fr.mapper.OrderMapper;
import com.fr.service.CartService;
import com.fr.service.GoodsService;
import com.fr.service.OrderService;
import com.fr.service.RiderService;

/**
 * 订单控制器
 * 所属模块：订单管理模块
 * 处理订单相关的请求，包括创建订单（支付）、我的订单、订单详情、取消订单、确认收货等功能
 * 请求路径前缀：/order
 */
@Controller
@RequestMapping("/order")
public class OrderController {
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	CartService cartService;
	
	@Autowired
	OrderMapper orderMapper;
	
	@Autowired
	OrderItemMapper orderItemMapper;
	
	@Autowired
	GoodsService goodsService;
	
	@Autowired
	RiderService riderService;
	
	@Autowired
	NotificationMapper notificationMapper;
	
	/**
	 * 订单支付/创建订单方法
	 * 将购物车中的商品生成订单，创建订单项，清空购物车，并向骑手发送新订单通知
	 * @param request HttpServletRequest请求对象，包含payType（支付方式）、name（收货人姓名）、phone（收货人电话）、address（收货地址）等参数
	 * @return ModelAndView 支付成功跳转到商品列表，失败返回相应页面并提示错误信息
	 */
	@RequestMapping("/pay")
	public ModelAndView pay(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		// 获取当前登录用户
		User user = (User) request.getSession().getAttribute("user");
		
		// 未登录则跳转到登录页
		if (user == null) {
			modelAndView.addObject("msg", "请先登录！");
			modelAndView.setViewName("login");
			return modelAndView;
		}
		
		// 获取支付方式，默认为1
		String payType = request.getParameter("payType");
		if (payType == null || payType.isEmpty()) {
			payType = "1";
		}
		
		// 查询用户购物车
		QueryWrapper<Cart> cartQueryWrapper = new QueryWrapper<>();
		cartQueryWrapper.eq("user_name", user.getUsername());
		List<Cart> cartList = cartService.findCart(cartQueryWrapper);
		
		// 购物车为空则返回
		if (cartList.isEmpty()) {
			request.getSession().setAttribute("msg", "购物车为空！");
			modelAndView.setViewName("redirect:/cart/cartList");
			return modelAndView;
		}
		
		// 使用时间戳生成订单号
		long orderId = System.currentTimeMillis();
		double totalAmount = 0;
		int totalCount = 0;
		
		// 计算订单总金额和商品总数
		for (Cart cart : cartList) {
			totalAmount += cart.getTotal_price();
			totalCount += cart.getCount();
		}
		
		// 如果总金额为0，重新计算（防止数据异常）
		if (totalAmount <= 0) {
			for (Cart cart : cartList) {
				totalAmount += Double.parseDouble(cart.getGoodsPrice()) * cart.getCount();
			}
		}
		
		// 获取收货信息，未填写则使用用户默认信息
		String name = request.getParameter("name");
		String phone = request.getParameter("phone");
		String address = request.getParameter("address");
		
		// 构建订单对象（状态3表示待配送）
		Order order = new Order();
		order.setId(orderId);
		order.setTotal(totalAmount);
		order.setAmount(totalCount);
		order.setStatus(3);
		order.setPaytype(Integer.parseInt(payType));
		order.setName(name != null && !name.isEmpty() ? name : user.getUsername());
		order.setPhone(phone != null && !phone.isEmpty() ? phone : user.getPhone());
		order.setAddress(address != null && !address.isEmpty() ? address : user.getAddress());
		order.setDatetime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		order.setUserId(user.getId());
		
		// 插入订单
		orderService.insertOrder(order);
		
		// 向所有骑手发送新订单通知
		sendNewOrderNotification(orderId, totalAmount);
		
		// 插入订单项
		for (Cart cart : cartList) {
			OrderItem orderItem = new OrderItem();
			orderItem.setPrice(Double.parseDouble(cart.getGoodsPrice()));
			orderItem.setAmount(cart.getCount());
			orderItem.setGoodsId(cart.getGoodId());
			orderItem.setOrderId(orderId);
			orderService.insertOrderItem(orderItem);
		}
		
		// 清空用户购物车
		cartService.deleteCartByUserName(user.getUsername());
		
		// 支付成功，跳转到商品列表
		request.getSession().setAttribute("msg", "支付成功！");
		modelAndView.setViewName("redirect:/goods/goodsList");
		return modelAndView;
	}
	
	/**
	 * 我的订单列表方法
	 * 查询当前用户的订单列表，支持按订单号或收货人姓名搜索，分页显示
	 * @param request HttpServletRequest请求对象，包含keyword（搜索关键词）、page（页码）等参数
	 * @return ModelAndView 返回我的订单列表页面
	 */
	@RequestMapping("/myOrders")
	public ModelAndView myOrders(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		// 获取当前登录用户
		User user = (User) request.getSession().getAttribute("user");
		
		// 未登录则跳转到登录页
		if (user == null) {
			modelAndView.addObject("msg", "请先登录！");
			modelAndView.setViewName("login");
			return modelAndView;
		}
		
		// 获取搜索关键词
		String keyword = request.getParameter("keyword");
		
		// 分页参数处理
		String pageStr = request.getParameter("page");
		int page = pageStr != null ? Integer.parseInt(pageStr) : 1;
		int pageSize = 5;
		
		// 构建订单查询条件，只查询当前用户的订单
		QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
		orderQueryWrapper.eq("user_id", user.getId());
		
		// 按关键词搜索（支持订单号精确查询和收货人姓名模糊查询）
		if (keyword != null && !keyword.isEmpty()) {
			try {
				long orderId = Long.parseLong(keyword);
				orderQueryWrapper.eq("id", orderId);
			} catch (NumberFormatException e) {
				orderQueryWrapper.like("name", keyword);
			}
		}
		
		// 按下单时间倒序排列
		orderQueryWrapper.orderByDesc("datetime");
		List<Order> orders = orderMapper.selectList(orderQueryWrapper);
		
		// 处理每个订单的商品明细
		for (Order order : orders) {
			QueryWrapper<OrderItem> itemQueryWrapper = new QueryWrapper<>();
			itemQueryWrapper.eq("order_id", order.getId());
			List<OrderItem> items = orderItemMapper.selectList(itemQueryWrapper);
			
			List<Map<String, Object>> itemDetails = new ArrayList<>();
			double orderTotal = 0;
			for (OrderItem item : items) {
				// 查询商品信息
				QueryWrapper<Goods> goodsQueryWrapper = new QueryWrapper<>();
				goodsQueryWrapper.eq("id", item.getGoodsId());
				List<Goods> goodsList = goodsService.findGoods(goodsQueryWrapper);
				
				Map<String, Object> detail = new HashMap<>();
				if (!goodsList.isEmpty()) {
					Goods goods = goodsList.get(0);
					detail.put("goodsName", goods.getName());
					detail.put("goodsCover", goods.getCover());
				}
				detail.put("price", item.getPrice());
				detail.put("amount", item.getAmount());
				itemDetails.add(detail);
				orderTotal += item.getPrice() * item.getAmount();
			}
			order.setItems(itemDetails);
			// 如果订单总金额为0，重新计算
			if (order.getTotal() <= 0) {
				order.setTotal(orderTotal);
			}
		}
		
		// 分页处理
		int totalItems = orders.size();
		int totalPages = (int) Math.ceil((double) totalItems / pageSize);
		
		if (page < 1) page = 1;
		if (page > totalPages && totalPages > 0) page = totalPages;
		
		int startIndex = (page - 1) * pageSize;
		int endIndex = Math.min(startIndex + pageSize, totalItems);
		List<Order> pageOrders = totalItems > 0 ? orders.subList(startIndex, endIndex) : new ArrayList<>();
		
		modelAndView.addObject("orders", pageOrders);
		modelAndView.addObject("keyword", keyword);
		modelAndView.addObject("totalItems", totalItems);
		modelAndView.addObject("totalPages", totalPages);
		modelAndView.addObject("currentPage", page);
		modelAndView.setViewName("myOrders");
		return modelAndView;
	}
	
	/**
	 * 取消订单方法
	 * 取消指定订单，将订单商品退回购物车，只有未取货的订单才能取消
	 * @param request HttpServletRequest请求对象，包含orderId（订单ID）参数
	 * @return ModelAndView 重定向到我的订单列表页面
	 */
	@RequestMapping("/cancelOrder")
	public ModelAndView cancelOrder(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		// 获取当前登录用户
		User user = (User) request.getSession().getAttribute("user");
		
		// 未登录则跳转到登录页
		if (user == null) {
			modelAndView.addObject("msg", "请先登录！");
			modelAndView.setViewName("login");
			return modelAndView;
		}
		
		// 获取订单ID
		String orderIdStr = request.getParameter("orderId");
		if (orderIdStr == null || orderIdStr.isEmpty()) {
			request.getSession().setAttribute("msg", "订单ID不能为空！");
			modelAndView.setViewName("redirect:/order/myOrders");
			return modelAndView;
		}
		
		long orderId = Long.parseLong(orderIdStr);
		
		// 查询订单
		Order order = orderMapper.selectById(orderId);
		if (order == null) {
			request.getSession().setAttribute("msg", "订单不存在！");
			modelAndView.setViewName("redirect:/order/myOrders");
			return modelAndView;
		}
		
		// 只有状态为2（已付款）或3（待配送）的订单才能取消
		if (order.getStatus() != 2 && order.getStatus() != 3) {
			request.getSession().setAttribute("msg", "只有未取货的订单才能取消！");
			modelAndView.setViewName("redirect:/order/myOrders");
			return modelAndView;
		}
		
		// 查询订单项
		QueryWrapper<OrderItem> itemQueryWrapper = new QueryWrapper<>();
		itemQueryWrapper.eq("order_id", orderId);
		List<OrderItem> items = orderItemMapper.selectList(itemQueryWrapper);
		
		// 将订单商品退回购物车
		for (OrderItem item : items) {
			Cart cart = new Cart();
			cart.setUserName(user.getUsername());
			cart.setGoodId(item.getGoodsId());
			cart.setCount(item.getAmount());
			cart.setGoodsPrice(String.valueOf(item.getPrice()));
			cart.setTotal_price(item.getPrice() * item.getAmount());
			
			// 查询商品信息用于填充购物车
			QueryWrapper<Goods> goodsQueryWrapper = new QueryWrapper<>();
			goodsQueryWrapper.eq("id", item.getGoodsId());
			List<Goods> goodsList = goodsService.findGoods(goodsQueryWrapper);
			if (!goodsList.isEmpty()) {
				Goods goods = goodsList.get(0);
				cart.setGoodsName(goods.getName());
				cart.setGoodsCover(goods.getCover());
			}
			
			cartService.addCart(cart);
		}
		
		// 更新订单状态为5（已取消）
		LambdaUpdateWrapper<Order> updateWrapper = new LambdaUpdateWrapper<>();
		updateWrapper.eq(Order::getId, orderId).set(Order::getStatus, 5);
		orderMapper.update(null, updateWrapper);
		
		request.getSession().setAttribute("msg", "订单已取消，商品已退回购物车！");
		modelAndView.setViewName("redirect:/order/myOrders");
		return modelAndView;
	}
	
	/**
	 * 确认收货方法
	 * 用户确认收到货物，将订单状态更新为已完成
	 * @param request HttpServletRequest请求对象，包含orderId（订单ID）参数
	 * @return ModelAndView 重定向到我的订单列表页面
	 */
	@RequestMapping("/confirmReceipt")
	public ModelAndView confirmReceipt(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		// 获取当前登录用户
		User user = (User) request.getSession().getAttribute("user");
		
		// 未登录则跳转到登录页
		if (user == null) {
			modelAndView.addObject("msg", "请先登录！");
			modelAndView.setViewName("login");
			return modelAndView;
		}
		
		// 获取订单ID
		String orderIdStr = request.getParameter("orderId");
		if (orderIdStr == null || orderIdStr.isEmpty()) {
			request.getSession().setAttribute("msg", "订单ID不能为空！");
			modelAndView.setViewName("redirect:/order/myOrders");
			return modelAndView;
		}
		
		long orderId = Long.parseLong(orderIdStr);
		
		// 查询订单
		Order order = orderMapper.selectById(orderId);
		if (order == null) {
			request.getSession().setAttribute("msg", "订单不存在！");
			modelAndView.setViewName("redirect:/order/myOrders");
			return modelAndView;
		}
		
		// 只有状态为3（待配送）、7（配送中）或4（已完成）的订单才能确认收货
		if (order.getStatus() != 3 && order.getStatus() != 7 && order.getStatus() != 4) {
			request.getSession().setAttribute("msg", "只能确认待配送、配送中或已完成订单的收货！");
			modelAndView.setViewName("redirect:/order/myOrders");
			return modelAndView;
		}
		
		// 更新订单状态为4（已完成）
		LambdaUpdateWrapper<Order> updateWrapper = new LambdaUpdateWrapper<>();
		updateWrapper.eq(Order::getId, orderId).set(Order::getStatus, 4);
		orderMapper.update(null, updateWrapper);
		
		request.getSession().setAttribute("msg", "确认收货成功！订单已完成！");
		modelAndView.setViewName("redirect:/order/myOrders");
		return modelAndView;
	}

	/**
	 * 订单详情方法
	 * 查询指定订单的详细信息，包括商品明细，只能查看自己的订单
	 * @param request HttpServletRequest请求对象，包含orderId（订单ID）参数
	 * @return ModelAndView 返回订单详情页面
	 */
	@RequestMapping("/orderDetail")
	public ModelAndView orderDetail(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		// 获取当前登录用户
		User user = (User) request.getSession().getAttribute("user");   

		// 未登录则跳转到登录页
		if (user == null) {
			modelAndView.addObject("msg", "请先登录！");        
			modelAndView.setViewName("login");
			return modelAndView;
		}

		// 获取订单ID
		String orderIdStr = request.getParameter("orderId");
		if (orderIdStr == null || orderIdStr.isEmpty()) {
			request.getSession().setAttribute("msg", "订单ID不能为空！");
			modelAndView.setViewName("redirect:/order/myOrders");   
			return modelAndView;
		}

		long orderId = Long.parseLong(orderIdStr);

		// 查询订单
		Order order = orderMapper.selectById(orderId);
		if (order == null) {
			request.getSession().setAttribute("msg", "订单不存在！");
			modelAndView.setViewName("redirect:/order/myOrders");   
			return modelAndView;
		}

		// 验证订单归属，只能查看自己的订单
		if (order.getUserId() != user.getId()) {
			request.getSession().setAttribute("msg", "您没有权限查看此订单！");
			modelAndView.setViewName("redirect:/order/myOrders");   
			return modelAndView;
		}

		// 查询订单项
		QueryWrapper<OrderItem> itemQueryWrapper = new QueryWrapper<>();
		itemQueryWrapper.eq("order_id", orderId);
		List<OrderItem> items = orderItemMapper.selectList(itemQueryWrapper);

		// 处理商品明细和计算订单总金额
		List<Map<String, Object>> itemDetails = new ArrayList<>();
		double orderTotal = 0;
		for (OrderItem item : items) {
			QueryWrapper<Goods> goodsQueryWrapper = new QueryWrapper<>();
			goodsQueryWrapper.eq("id", item.getGoodsId());  
			List<Goods> goodsList = goodsService.findGoods(goodsQueryWrapper);

			Map<String, Object> detail = new HashMap<>();   
			if (!goodsList.isEmpty()) {
				Goods goods = goodsList.get(0);
				detail.put("goodsName", goods.getName());
				detail.put("goodsCover", goods.getCover());
			}
			detail.put("price", item.getPrice());
			detail.put("amount", item.getAmount());
			itemDetails.add(detail);
			orderTotal += item.getPrice() * item.getAmount();
		}
		order.setItems(itemDetails);
		// 如果订单总金额为0，重新计算
		if (order.getTotal() <= 0) {
			order.setTotal(orderTotal);
		}

		modelAndView.addObject("order", order);
		modelAndView.addObject("statusText", getStatusText(order.getStatus()));
		modelAndView.setViewName("orderDetail");
		return modelAndView;
	}

	/**
	 * 获取订单状态文本描述
	 * @param status 订单状态码
	 * @return 订单状态中文描述
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
	 * 向所有骑手发送新订单通知
	 */
	private void sendNewOrderNotification(long orderId, double totalAmount) {
		try {
			List<Rider> riders = riderService.getAllRiders();
			
			for (Rider rider : riders) {
				if (rider.getStatus() == 1) {
					Notification notification = new Notification();
					notification.setRiderId(rider.getId());
					notification.setTitle("新订单待接单");
					notification.setContent("有新订单需要配送，订单号：#" + orderId + "，金额：¥" + String.format("%.2f", totalAmount));
					notification.setType("order");
					notification.setOrderId(orderId);
					notification.setIsRead(0);
					notification.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
					notificationMapper.insert(notification);
				}
			}
		} catch (Exception e) {
			// 通知发送失败不影响订单创建
		}
	}
}
