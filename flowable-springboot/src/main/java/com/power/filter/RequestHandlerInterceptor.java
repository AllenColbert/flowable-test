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
public class RequestHandlerInterceptor  implements HandlerInterceptor {


    @Autowired
    HttpSession session;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        System.out.println(request.getServletPath());
        //System.out.println("拦截器处sessionId："+session.getId());
        Object user = session.getAttribute("user");
        if (user != null){
            System.out.println(((User)user).getId());
            return true;
        }
       // System.out.println("拦截成功");
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
