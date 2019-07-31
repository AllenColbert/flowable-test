package com.power.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
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
                        "/app/**"
                       );
    }

}
