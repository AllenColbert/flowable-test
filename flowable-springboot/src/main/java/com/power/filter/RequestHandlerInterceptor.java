package com.power.filter;

import org.flowable.idm.api.User;
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

    //TODO 这里只是做了登录判断，没有考虑用户未登录状态下的注册活动
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        System.out.println(request.getServletPath());
        Object user = null;
        user = session.getAttribute("user");
        if (user != null) {
            System.out.println("当前登陆用户Id："+((User) user).getId());
            return true;
        }
        System.out.println("拦截到请求："+request.getServletPath()+"状态："+response.getStatus());
        return false;
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
