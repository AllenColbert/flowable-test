package com.power.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面跳转
 * @author xuyunfeng
 * @date 2019/7/31 19:07
 */
@Controller
public class Index {

    @GetMapping("/")
    public String index(){
        return "index";
    }


    @GetMapping("login")
    public String login(){
        return "login";
    }

}
