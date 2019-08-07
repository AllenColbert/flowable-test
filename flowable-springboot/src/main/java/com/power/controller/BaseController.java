package com.power.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 页面跳转
 * @author xuyunfeng
 * @date 2019/7/31 19:07
 */
@Controller
public class BaseController {

    public final static Integer SUCCESS_CODE = 200;

    @GetMapping("/")
    public String index(){
        return "index";
    }


    @GetMapping("login")
    public String login(){
        return "login";
    }

    @GetMapping("webTest")
    public String webTest(){
        return "web-test";
    }

}
