package com.fr.config;

import com.fr.javaBean.Rider;
import com.fr.javaBean.User;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义登录拦截器
 * 
 * 该拦截器实现 HandlerInterceptor 接口，用于拦截用户请求并进行登录验证。
 * 主要功能：
 * 1. 在请求处理前检查用户是否已登录
 * 2. 对登录、注册等公开路径直接放行
 * 3. 未登录用户重定向到登录页面
 * 4. 支持普通用户和骑手两种登录身份
 *
 * @author CakeShop Team
 */
public class CustomInterceptor implements HandlerInterceptor {

    /**
     * 前置处理方法 - 在请求处理之前执行
     * 
     * 用于在请求到达 Controller 之前进行登录验证。
     * 如果请求路径是公开路径（登录、注册、静态资源），直接放行。
     * 否则检查 Session 中是否存在登录用户信息，存在则放行，不存在则重定向到登录页。
     *
     * @param request  HTTP 请求对象，用于获取请求信息和 Session
     * @param response HTTP 响应对象，用于重定向等操作
     * @param handler  目标处理器（Controller 方法）
     * @return boolean 返回 true 表示放行，返回 false 表示拦截请求
     * @throws Exception 处理过程中可能抛出的异常
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取当前请求的 URI 路径
        String requestUri = request.getRequestURI();
        
        // 判断是否为公开路径，这些路径不需要登录即可访问
        if (requestUri.contains("/user/login") || requestUri.contains("/user/register") || 
            requestUri.contains("/register") || requestUri.contains("/login.html") ||
            requestUri.contains("/register.html") || requestUri.contains("/static/")) {
            // 公开路径，直接放行
            return true;
        }
        
        // 从 Session 中获取普通用户登录信息
        User user = (User) request.getSession().getAttribute("user");
        // 从 Session 中获取骑手登录信息
        Rider rider = (Rider) request.getSession().getAttribute("rider");
        
        // 判断用户或骑手是否已登录
        if (user != null || rider != null) {
            // 已登录，输出日志并放行请求
            System.out.println("请求之前,放行");
            return true;
        } else {
            // 未登录，输出拦截日志
            System.out.println("拦截");
            // 设置提示信息，告知用户未登录
            request.setAttribute("msg","您还未登陆");
            // 设置请求字符编码为 UTF-8，防止中文乱码
            request.setCharacterEncoding("UTF-8");
            // 设置响应内容类型和字符编码，确保页面正确显示中文
            response.setContentType("text/html;charset=UTF-8");
            // 重定向到登录页面
            response.sendRedirect("/user/login");
            // 拦截请求，不继续执行
            return false;
        }

    }

    /**
     * 后置处理方法 - 在请求处理之后、视图渲染之前执行
     * 
     * 该方法在 Controller 方法执行完成后，视图渲染之前被调用。
     * 可以对 ModelAndView 进行修改，如添加公共数据等。
     *
     * @param request      HTTP 请求对象
     * @param response     HTTP 响应对象
     * @param handler      目标处理器（Controller 方法）
     * @param modelAndView 视图和模型对象，可能为 null
     * @throws Exception 处理过程中可能抛出的异常
     */
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        // 输出请求处理完成的日志
        System.out.println("请求之后");
    }

    /**
     * 完成后处理方法 - 在整个请求结束之后执行
     * 
     * 该方法在视图渲染完成后被调用，主要用于资源清理工作。
     * 无论请求处理是否成功，该方法都会被执行。
     *
     * @param request  HTTP 请求对象
     * @param response HTTP 响应对象
     * @param handler  目标处理器（Controller 方法）
     * @param ex       请求处理过程中抛出的异常，没有异常则为 null
     * @throws Exception 处理过程中可能抛出的异常
     */
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        // 输出整个请求结束的日志
        System.out.println("整个请求结束之后");
    }

}
