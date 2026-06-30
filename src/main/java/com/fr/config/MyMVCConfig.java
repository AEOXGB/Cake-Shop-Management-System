package com.fr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;


/**
 * Spring MVC 配置类
 * 
 * 该配置类实现 WebMvcConfigurer 接口，用于自定义 Spring MVC 的配置。
 * 主要配置内容包括：
 * 1. 视图控制器：配置无需后端逻辑的页面跳转
 * 2. 拦截器：注册自定义拦截器并配置拦截路径
 * 3. 资源处理器：配置静态资源的映射路径
 * 4. 本地化解析器：注入自定义的区域解析器
 *
 * @author CakeShop Team
 */
@Configuration
public class MyMVCConfig implements WebMvcConfigurer {

    /**
     * 添加视图控制器
     * 
     * 用于配置纯展示页面的页面跳转，这些页面只需要跳转而不需要后端处理逻辑。
     * 适用于登录页、注册页、首页、帮助页等无数据查询的页面。
     *
     * @param registry 视图控制器注册表，用于注册视图控制器
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 根路径跳转到登录页面
        registry.addViewController("/").setViewName("login");
        // login.html 路径跳转到登录页面
        registry.addViewController("/login.html").setViewName("login");
        // index.html 路径跳转到首页
        registry.addViewController("/index.html").setViewName("index");
        // register.html 路径跳转到注册页面
        registry.addViewController("/register.html").setViewName("register");

    }


    /**
     * 配置本地化解析器
     * 
     * 将自定义的 MyLocaleResolver 注入到 Spring 容器中，
     * 替换默认的 LocaleResolver，实现通过请求参数控制语言区域。
     *
     * @return LocaleResolver 自定义的本地化解析器实例
     */
    @Bean
    public LocaleResolver localeResolver(){
        // 返回自定义的本地化解析器实例
        return new MyLocaleResolver();
    }

    /**
     * 添加拦截器
     * 
     * 注册自定义拦截器 CustomInterceptor，并配置拦截路径和放行路径。
     * 拦截器用于在请求处理前后进行一些通用处理，如登录验证、权限检查等。
     *
     * @param registry 拦截器注册表，用于注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册自定义拦截器，拦截所有请求
        registry.addInterceptor(new CustomInterceptor()).addPathPatterns("/**")
                // 配置放行路径，这些路径不经过拦截器
                .excludePathPatterns("/","/login.html","/register.html","/user/login","/user/register","/css/**","/js/**","/error","/images/**","/picture/**");

    }

    /**
     * 添加资源处理器
     * 
     * 配置静态资源的映射路径，将 URL 路径映射到实际的文件系统路径。
     * 主要用于映射外部 picture 目录，避免重启服务器后图片丢失。
     *
     * @param registry 资源处理器注册表，用于注册资源处理器
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 构建外部图片目录的绝对路径，使用 file: 前缀表示文件系统路径
        String picturePath = "file:" + System.getProperty("user.dir") + File.separator + "picture" + File.separator;
        // 配置 /picture/** 路径的资源映射
        registry.addResourceHandler("/picture/**")
                // 映射到外部文件系统的 picture 目录
                .addResourceLocations(picturePath)
                // 同时映射到 classpath 下的静态资源目录作为备选
                .addResourceLocations("classpath:/static/picture/");
    }


}
