package com.power.controller;

import com.alibaba.fastjson.JSON;
import com.power.util.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 页面跳转
 * @author xuyunfeng
 * @date 2019/7/31 19:07
 */
@Controller
public class BaseController {



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
    public Result receiveData(@RequestParam String formData,
                              @RequestParam Map<String,String> result){

        System.out.println("转译后传递的JSON字符串"+formData);
        //将字符串解析成map对象
        Map map  = (Map) JSON.parse(formData);
        //直接传递的map对象
        System.out.println("直接传递的map对象："+result);

        for (String s : result.keySet()) {
            System.out.println("直接从result中取值："+result.get(s));
        }

        for (Object o : map.keySet()) {
            System.out.println("从解析后的Map中取值："+map.get(o));
        }
        return Result.success(formData);
    }
}
