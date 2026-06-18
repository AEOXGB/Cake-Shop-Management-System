package com.fr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;


@Configuration
public class MyMVCConfig implements WebMvcConfigurer {
    /**
     * 登录页、注册页、首页、帮助页等纯展示、无数据查询的页面；
     * 只需要跳转，不需要后端处理逻辑的请求。
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("login");
        registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/index.html").setViewName("index");
        registry.addViewController("/register.html").setViewName("register");

    }


    //在mvc的配置类上注入bean！！！
    @Bean
    public LocaleResolver localeResolver(){
        return new MyLocaleResolver();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CustomInterceptor()).addPathPatterns("/**")
                .excludePathPatterns("/","/login.html","/register.html","/user/login","/user/register","/css/**","/js/**","/error","/images/**","/picture/**");

    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射外部picture目录，避免重启服务器丢失图片
        String picturePath = "file:" + System.getProperty("user.dir") + File.separator + "picture" + File.separator;
        registry.addResourceHandler("/picture/**")
                .addResourceLocations(picturePath)
                .addResourceLocations("classpath:/static/picture/");
    }


}
