package com.power.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 测试向Controller
 * @author xuyunfeng
 * @date 2019/7/31 19:07
 */
@Controller
public class Test {

    @GetMapping("test")
    public String login(){
        return "test";
    }
}
