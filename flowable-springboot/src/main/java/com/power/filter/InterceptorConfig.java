package com.power.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 本来想做拦截器的，用户未登录的时候拦截掉全部的请求，不过前端需要释放的资源太多了
 * @author : xuyunfeng
 * @date :   2019/7/19 14:36
 */
@Configuration
public class InterceptorConfig  implements WebMvcConfigurer {
    @Autowired
    private RequestHandlerInterceptor requestHandlerInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestHandlerInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/idm/login**",
                        "/task/processDiagram",
                        "/index.html",
                        "/**/*.html",
                        "/idm/logout",
                        "idm/userRegister",
                        "/task/processDiagram",
                        "/display/**",
                        "/display-cmmn/**",
                        "/editor-app/**",
                        "/fonts/**",
                        "/i18n/**",
                        "/images/**",
                        "/libs/**",
                        "/scripts/**",
                        "/styles/**",
                        "/views/**",
                        "/favicon.ico/**",
                        "/manifest.json",
                        "/browserconfig.xml",
                        "/app/**",
                        "/css/**"
                       );
    }

}
