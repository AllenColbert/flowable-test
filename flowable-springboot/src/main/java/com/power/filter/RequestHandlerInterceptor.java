package com.power.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author : xuyunfeng
 * @date :   2019/7/19 14:05
 */

@Component
public class RequestHandlerInterceptor implements HandlerInterceptor {

    @Autowired
    private HttpSession session;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String servletPath = request.getServletPath();
        //释放静态资源文件
        if (servletPath.endsWith(".png")||servletPath.endsWith(".json")||servletPath.endsWith("css")||
                servletPath.endsWith("js")||servletPath.endsWith("woff")||servletPath.endsWith("ttf")){
            return true;
        }

        System.out.println(request.getServletPath());
        Object user = null;
        user = session.getAttribute("user");

        if (user != null) {
            return true;
        }
        System.out.println("拦截到请求："+servletPath);
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {

    }

}
