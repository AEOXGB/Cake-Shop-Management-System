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
	
	@RequestMapping("/pay")
	public ModelAndView pay(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		User user = (User) request.getSession().getAttribute("user");
		
		if (user == null) {
			modelAndView.addObject("msg", "请先登录！");
			modelAndView.setViewName("login");
			return modelAndView;
		}
		
		String payType = request.getParameter("payType");
		if (payType == null || payType.isEmpty()) {
			payType = "1";
		}
		
		QueryWrapper<Cart> cartQueryWrapper = new QueryWrapper<>();
		cartQueryWrapper.eq("user_name", user.getUsername());
		List<Cart> cartList = cartService.findCart(cartQueryWrapper);
		
		if (cartList.isEmpty()) {
			request.getSession().setAttribute("msg", "购物车为空！");
			modelAndView.setViewName("redirect:/cart/cartList");
			return modelAndView;
		}
		
		long orderId = System.currentTimeMillis();
		double totalAmount = 0;
		int totalCount = 0;
		
		for (Cart cart : cartList) {
			totalAmount += cart.getTotal_price();
			totalCount += cart.getCount();
		}
		
		if (totalAmount <= 0) {
			for (Cart cart : cartList) {
				totalAmount += Double.parseDouble(cart.getGoodsPrice()) * cart.getCount();
			}
		}
		
		String name = request.getParameter("name");
		String phone = request.getParameter("phone");
		String address = request.getParameter("address");
		
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
		
		orderService.insertOrder(order);
		
		// 向所有骑手发送新订单通知
		sendNewOrderNotification(orderId, totalAmount);
		
		for (Cart cart : cartList) {
			OrderItem orderItem = new OrderItem();
			orderItem.setPrice(Double.parseDouble(cart.getGoodsPrice()));
			orderItem.setAmount(cart.getCount());
			orderItem.setGoodsId(cart.getGoodId());
			orderItem.setOrderId(orderId);
			orderService.insertOrderItem(orderItem);
		}
		
		cartService.deleteCartByUserName(user.getUsername());
		
		request.getSession().setAttribute("msg", "支付成功！");
		modelAndView.setViewName("redirect:/goods/goodsList");
		return modelAndView;
	}
	
	@RequestMapping("/myOrders")
	public ModelAndView myOrders(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		User user = (User) request.getSession().getAttribute("user");
		
		if (user == null) {
			modelAndView.addObject("msg", "请先登录！");
			modelAndView.setViewName("login");
			return modelAndView;
		}
		
		String keyword = request.getParameter("keyword");
		
		// 分页参数
		String pageStr = request.getParameter("page");
		int page = pageStr != null ? Integer.parseInt(pageStr) : 1;
		int pageSize = 5;
		
		QueryWrapper<Order> orderQueryWrapper = new QueryWrapper<>();
		orderQueryWrapper.eq("user_id", user.getId());
		
		if (keyword != null && !keyword.isEmpty()) {
			try {
				long orderId = Long.parseLong(keyword);
				orderQueryWrapper.eq("id", orderId);
			} catch (NumberFormatException e) {
				orderQueryWrapper.like("name", keyword);
			}
		}
		
		orderQueryWrapper.orderByDesc("datetime");
		List<Order> orders = orderMapper.selectList(orderQueryWrapper);
		
		// 处理订单商品明细
		for (Order order : orders) {
			QueryWrapper<OrderItem> itemQueryWrapper = new QueryWrapper<>();
			itemQueryWrapper.eq("order_id", order.getId());
			List<OrderItem> items = orderItemMapper.selectList(itemQueryWrapper);
			
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
	
	@RequestMapping("/cancelOrder")
	public ModelAndView cancelOrder(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		User user = (User) request.getSession().getAttribute("user");
		
		if (user == null) {
			modelAndView.addObject("msg", "请先登录！");
			modelAndView.setViewName("login");
			return modelAndView;
		}
		
		String orderIdStr = request.getParameter("orderId");
		if (orderIdStr == null || orderIdStr.isEmpty()) {
			request.getSession().setAttribute("msg", "订单ID不能为空！");
			modelAndView.setViewName("redirect:/order/myOrders");
			return modelAndView;
		}
		
		long orderId = Long.parseLong(orderIdStr);
		
		Order order = orderMapper.selectById(orderId);
		if (order == null) {
			request.getSession().setAttribute("msg", "订单不存在！");
			modelAndView.setViewName("redirect:/order/myOrders");
			return modelAndView;
		}
		
		if (order.getStatus() != 2 && order.getStatus() != 3) {
			request.getSession().setAttribute("msg", "只有未取货的订单才能取消！");
			modelAndView.setViewName("redirect:/order/myOrders");
			return modelAndView;
		}
		
		QueryWrapper<OrderItem> itemQueryWrapper = new QueryWrapper<>();
		itemQueryWrapper.eq("order_id", orderId);
		List<OrderItem> items = orderItemMapper.selectList(itemQueryWrapper);
		
		for (OrderItem item : items) {
			Cart cart = new Cart();
			cart.setUserName(user.getUsername());
			cart.setGoodId(item.getGoodsId());
			cart.setCount(item.getAmount());
			cart.setGoodsPrice(String.valueOf(item.getPrice()));
			cart.setTotal_price(item.getPrice() * item.getAmount());
			
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
		
		LambdaUpdateWrapper<Order> updateWrapper = new LambdaUpdateWrapper<>();
		updateWrapper.eq(Order::getId, orderId).set(Order::getStatus, 5);
		orderMapper.update(null, updateWrapper);
		
		request.getSession().setAttribute("msg", "订单已取消，商品已退回购物车！");
		modelAndView.setViewName("redirect:/order/myOrders");
		return modelAndView;
	}
	
	@RequestMapping("/confirmReceipt")
	public ModelAndView confirmReceipt(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		User user = (User) request.getSession().getAttribute("user");
		
		if (user == null) {
			modelAndView.addObject("msg", "请先登录！");
			modelAndView.setViewName("login");
			return modelAndView;
		}
		
		String orderIdStr = request.getParameter("orderId");
		if (orderIdStr == null || orderIdStr.isEmpty()) {
			request.getSession().setAttribute("msg", "订单ID不能为空！");
			modelAndView.setViewName("redirect:/order/myOrders");
			return modelAndView;
		}
		
		long orderId = Long.parseLong(orderIdStr);
		
		Order order = orderMapper.selectById(orderId);
		if (order == null) {
			request.getSession().setAttribute("msg", "订单不存在！");
			modelAndView.setViewName("redirect:/order/myOrders");
			return modelAndView;
		}
		
		if (order.getStatus() != 3 && order.getStatus() != 7 && order.getStatus() != 4) {
			request.getSession().setAttribute("msg", "只能确认待配送、配送中或已完成订单的收货！");
			modelAndView.setViewName("redirect:/order/myOrders");
			return modelAndView;
		}
		
		LambdaUpdateWrapper<Order> updateWrapper = new LambdaUpdateWrapper<>();
		updateWrapper.eq(Order::getId, orderId).set(Order::getStatus, 4);
		orderMapper.update(null, updateWrapper);
		
		request.getSession().setAttribute("msg", "确认收货成功！订单已完成！");
		modelAndView.setViewName("redirect:/order/myOrders");
		return modelAndView;
	}

	@RequestMapping("/orderDetail")
	public ModelAndView orderDetail(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		User user = (User) request.getSession().getAttribute("user");   

		if (user == null) {
			modelAndView.addObject("msg", "请先登录！");        
			modelAndView.setViewName("login");
			return modelAndView;
		}

		String orderIdStr = request.getParameter("orderId");
		if (orderIdStr == null || orderIdStr.isEmpty()) {
			request.getSession().setAttribute("msg", "订单ID不能为空！");
			modelAndView.setViewName("redirect:/order/myOrders");   
			return modelAndView;
		}

		long orderId = Long.parseLong(orderIdStr);

		Order order = orderMapper.selectById(orderId);
		if (order == null) {
			request.getSession().setAttribute("msg", "订单不存在！");
			modelAndView.setViewName("redirect:/order/myOrders");   
			return modelAndView;
		}

		if (order.getUserId() != user.getId()) {
			request.getSession().setAttribute("msg", "您没有权限查看此订单！");
			modelAndView.setViewName("redirect:/order/myOrders");   
			return modelAndView;
		}

		QueryWrapper<OrderItem> itemQueryWrapper = new QueryWrapper<>();
		itemQueryWrapper.eq("order_id", orderId);
		List<OrderItem> items = orderItemMapper.selectList(itemQueryWrapper);

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
		if (order.getTotal() <= 0) {
			order.setTotal(orderTotal);
		}

		modelAndView.addObject("order", order);
		modelAndView.addObject("statusText", getStatusText(order.getStatus()));
		modelAndView.setViewName("orderDetail");
		return modelAndView;
	}

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
