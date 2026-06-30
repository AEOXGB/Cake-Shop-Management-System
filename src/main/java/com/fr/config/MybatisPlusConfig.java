package com.fr.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类
 * 
 * 该配置类用于配置 MyBatis-Plus 框架的相关功能。
 * 核心功能是注册 MyBatis-Plus 的分页拦截器，实现数据库分页查询功能。
 * 通过配置分页插件，可以在使用 MyBatis-Plus 进行查询时自动处理分页逻辑。
 *
 * @author CakeShop Team
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 配置 MyBatis-Plus 分页插件
     * 
     * 创建并配置 MybatisPlusInterceptor 拦截器，添加分页内部拦截器。
     * 分页插件会自动拦截 SQL 查询，根据传入的分页参数生成分页 SQL。
     * 指定数据库类型为 MySQL，以确保分页语法的正确性。
     *
     * @return MybatisPlusInterceptor 配置好的 MyBatis-Plus 拦截器实例
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        // 创建 MyBatis-Plus 主拦截器实例
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页内部拦截器，并指定数据库类型为 MySQL
        // PaginationInnerInterceptor 会自动处理分页 SQL 的生成
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        // 返回配置好的拦截器实例，注册到 Spring 容器中
        return interceptor;
    }
}
