package com.power.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 测试向Controller
 * @author xuyunfeng
 * @date 2019/7/31 19:07
 */
@Controller
public class Test {

    @Autowired

    @GetMapping("/")
    public String login(){
        return "login";
    }

    @GetMapping("releaseProcess")
    public String processList(){
        return "releaseProcess";
    }
}
