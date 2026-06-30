package com.fr.config;


import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * 自定义本地化解析器
 * 
 * 该类实现 LocaleResolver 接口，用于自定义区域（语言）解析逻辑。
 * 功能：通过请求参数 "l" 来动态切换语言环境，实现国际化功能。
 * 使用方式：在 URL 中携带参数 l=zh_CN 或 l=en_US 等，即可切换语言。
 * 该类需要在 Spring MVC 配置类中通过 @Bean 注解注入才能生效。
 *
 * @author CakeShop Team
 */
public class MyLocaleResolver implements LocaleResolver {

    /**
     * 解析区域信息
     * 
     * 从 HTTP 请求中解析出区域（语言）信息。
     * 如果请求参数中包含 "l" 参数，则根据该参数值创建对应的 Locale 对象；
     * 如果没有该参数，则使用系统默认的区域设置。
     * 参数格式：语言代码_国家代码，例如 zh_CN（中文-中国）、en_US（英文-美国）
     *
     * @param request HTTP 请求对象，用于获取请求参数
     * @return Locale 解析后的区域信息对象
     */
    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        // 从请求参数中获取区域信息参数，参数名为 "l"
        String l = request.getParameter("l");
        // 先获取系统默认的区域设置作为默认值
        Locale locale = Locale.getDefault();
        // 判断参数是否不为空且不为空字符串
        if(!StringUtils.isEmpty(l)){
            // 将参数按下划线分割，格式为：语言代码_国家代码
            String[] split = l.split("_");
            // 使用语言代码和国家代码创建新的 Locale 对象
            locale = new Locale(split[0],split[1]);
        }
        // 返回解析后的区域信息
        return locale;
    }

    /**
     * 设置区域信息
     * 
     * 该方法用于设置区域信息，但本实现不支持动态设置区域，
     * 因为区域信息是通过请求参数传递的，不需要持久化存储。
     * 如果需要实现 Session 或 Cookie 级别的区域切换，可以在此方法中实现。
     *
     * @param request  HTTP 请求对象
     * @param response HTTP 响应对象
     * @param locale   要设置的区域信息
     */
    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        // 空实现：本解析器不支持设置区域，仅通过请求参数解析
    }


}
