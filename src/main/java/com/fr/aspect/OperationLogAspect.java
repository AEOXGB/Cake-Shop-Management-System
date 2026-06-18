package com.fr.aspect;

import com.fr.javaBean.OperationLog;
import com.fr.javaBean.User;
import com.fr.mapper.OperationLogMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 操作日志切面
 * 自动拦截所有控制器的请求方法，记录操作日志到数据库
 * 支持管理员和普通用户的操作记录
 */
@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private OperationLogMapper operationLogMapper;

    /**
     * 切点：拦截 AdminController 中所有返回 String 或 ModelAndView 的公共方法
     */
    @Pointcut("execution(* com.fr.controller.AdminController.*(..))")
    public void adminPointcut() {
    }

    /**
     * 切点：拦截 UserController 中所有方法
     */
    @Pointcut("execution(* com.fr.controller.UserController.*(..))")
    public void userPointcut() {
    }

    /**
     * 切点：拦截 OrderController 中所有方法
     */
    @Pointcut("execution(* com.fr.controller.OrderController.*(..))")
    public void orderPointcut() {
    }

    /**
     * 切点：拦截 CartController 中所有方法
     */
    @Pointcut("execution(* com.fr.controller.CartController.*(..))")
    public void cartPointcut() {
    }

    /**
     * 切点：拦截 GoodsController 中所有方法
     */
    @Pointcut("execution(* com.fr.controller.GoodsController.*(..))")
    public void goodsPointcut() {
    }

    /**
     * 环绕通知：记录管理员操作日志
     */
    @Around("adminPointcut()")
    public Object aroundAdmin(ProceedingJoinPoint joinPoint) throws Throwable {
        return processLog(joinPoint, true);
    }

    /**
     * 环绕通知：记录用户操作日志
     */
    @Around("userPointcut() || orderPointcut() || cartPointcut() || goodsPointcut()")
    public Object aroundUser(ProceedingJoinPoint joinPoint) throws Throwable {
        return processLog(joinPoint, false);
    }

    /**
     * 处理日志记录
     * @param joinPoint 连接点
     * @param isAdmin 是否管理员操作
     */
    private Object processLog(ProceedingJoinPoint joinPoint, boolean isAdmin) throws Throwable {
        // 获取方法名
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        // 不需要记录日志的方法列表（纯页面展示）
        String[] excludeMethods = {"adminIndex", "goodsList", "goodsDetail", "orderList",
                "orderDetail", "orderStats", "userList", "userStats", "settings",
                "editGoods", "addGoods", "editUser", "setAdminPage", "adminSettings",
                "operationLogs", "myOrders", "cartList", "topSell", "newArrivals",
                "index"};

        for (String exclude : excludeMethods) {
            if (methodName.equals(exclude)) {
                return joinPoint.proceed();
            }
        }

        // 获取请求对象
        HttpServletRequest request = null;
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            request = attributes.getRequest();
        }

        // 执行原方法
        Object result = joinPoint.proceed();

        // 在方法执行后获取用户信息（登录操作会在执行过程中设置用户到session）
        User user = null;
        if (request != null && request.getSession() != null) {
            user = (User) request.getSession().getAttribute("user");
        }

        // 如果session中没有用户，尝试从请求参数获取用户名（用于登录操作）
        String username = "匿名用户";
        if (user != null) {
            username = user.getUsername();
        } else if (request != null && "userLogin".equals(methodName)) {
            // 登录操作：从请求参数获取用户名
            String paramUsername = request.getParameter("username");
            if (paramUsername != null && !paramUsername.trim().isEmpty()) {
                username = paramUsername;
            }
        }

        // 记录操作日志
        try {
            OperationLog log = new OperationLog();
            log.setUsername(username);
            log.setOperation(getOperationDescription(methodName, className));
            log.setMethod(className + "." + methodName);
            if (request != null) {
                log.setIp(getIpAddress(request));
                StringBuilder params = new StringBuilder();
                java.util.Map<String, String[]> paramMap = request.getParameterMap();
                for (java.util.Map.Entry<String, String[]> entry : paramMap.entrySet()) {
                    // 过滤敏感参数
                    if ("password".equalsIgnoreCase(entry.getKey()) || 
                        "newPassword".equalsIgnoreCase(entry.getKey()) ||
                        "confirmPassword".equalsIgnoreCase(entry.getKey()) ||
                        "oldPassword".equalsIgnoreCase(entry.getKey())) {
                        params.append(entry.getKey()).append("=******&");
                    } else {
                        params.append(entry.getKey()).append("=");
                        String[] values = entry.getValue();
                        if (values != null && values.length > 0) {
                            params.append(String.join(",", values));
                        }
                        params.append("&");
                    }
                }
                if (params.length() > 0) {
                    params.setLength(params.length() - 1);
                }
                log.setParams(params.toString());
            }
            log.setCreateTime(new Date());
            operationLogMapper.insert(log);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 根据方法名和类名生成操作描述
     */
    private String getOperationDescription(String methodName, String className) {
        switch (className) {
            case "AdminController":
                return getAdminOperation(methodName);
            case "UserController":
                return getUserOperation(methodName);
            case "OrderController":
                return getOrderOperation(methodName);
            case "CartController":
                return getCartOperation(methodName);
            case "GoodsController":
                return getGoodsOperation(methodName);
            default:
                return "执行操作: " + className + "." + methodName;
        }
    }

    /**
     * 管理员操作描述
     */
    private String getAdminOperation(String methodName) {
        switch (methodName) {
            case "saveGoods": return "保存商品";
            case "deleteGoods": return "删除商品";
            case "validateUser": return "审核用户";
            case "freezeUser": return "冻结用户";
            case "unfreezeUser": return "解冻用户";
            case "updateUser": return "修改用户信息";
            case "setAdmin": return "设置管理员权限";
            case "shipOrder": return "订单发货";
            case "changePassword": return "修改密码";
            case "saveType": return "保存分类";
            case "updateType": return "更新分类";
            case "deleteType": return "删除分类";
            default: return "管理员操作: " + methodName;
        }
    }

    /**
     * 用户操作描述
     */
    private String getUserOperation(String methodName) {
        switch (methodName) {
            case "register": return "用户注册";
            case "login": return "用户登录";
            case "loginout": return "用户退出";
            case "updateUser": return "修改个人信息";
            case "changePassword": return "修改密码";
            default: return "用户操作: " + methodName;
        }
    }

    /**
     * 订单操作描述
     */
    private String getOrderOperation(String methodName) {
        switch (methodName) {
            case "pay": return "订单支付";
            case "cancelOrder": return "取消订单";
            case "confirmReceipt": return "确认收货";
            default: return "订单操作: " + methodName;
        }
    }

    /**
     * 购物车操作描述
     */
    private String getCartOperation(String methodName) {
        switch (methodName) {
            case "addToCart": return "添加商品到购物车";
            case "deleteCart": return "删除购物车商品";
            case "changeCount": return "修改购物车数量";
            default: return "购物车操作: " + methodName;
        }
    }

    /**
     * 商品操作描述
     */
    private String getGoodsOperation(String methodName) {
        switch (methodName) {
            case "goodsList": return "浏览商品列表";
            case "goodsDetail": return "查看商品详情";
            case "topSell": return "查看热销商品";
            case "newArrivals": return "查看新品";
            default: return "商品操作: " + methodName;
        }
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}