package com.fr.config;

import com.fr.javaBean.Rider;
import com.fr.javaBean.User;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomInterceptor implements HandlerInterceptor {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        
        if (requestUri.contains("/user/login") || requestUri.contains("/user/register") || 
            requestUri.contains("/register") || requestUri.contains("/login.html") ||
            requestUri.contains("/register.html") || requestUri.contains("/static/")) {
            return true;
        }
        
        User user = (User) request.getSession().getAttribute("user");
        Rider rider = (Rider) request.getSession().getAttribute("rider");
        
        if (user != null || rider != null) {
            System.out.println("请求之前,放行");
            return true;
        } else {
            System.out.println("拦截");
            request.setAttribute("msg","您还未登陆");
            request.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=UTF-8");
            response.sendRedirect("/user/login");
            return false;
        }

    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        System.out.println("请求之后");
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        System.out.println("整个请求结束之后");
    }

}
