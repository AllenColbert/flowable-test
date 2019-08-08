package com.power.controller;

import com.alibaba.fastjson.JSON;
import com.power.util.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @PostMapping("data")
    @ResponseBody
    public Result receiveData(@RequestParam String formData){

        List<Map> parse = JSON.parseArray(formData, Map.class);
        System.out.println(parse);

        Map<String, String> hashMap = new HashMap<>();

        for (Map map : parse) {
            System.out.println(map);
        }

        System.out.println(hashMap);

        /*Map map  = (Map) JSON.parse(formData);*/
        return Result.success(hashMap);
    }
}
