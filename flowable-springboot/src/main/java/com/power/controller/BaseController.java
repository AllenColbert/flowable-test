package com.power.controller;

import com.alibaba.fastjson.JSON;
import com.power.service.PowerTaskService;
import com.power.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
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
@Api(value = "基本控制类，提供通用方法和部分测试中的方法" ,tags = {"通用接口"})
@Controller
public class BaseController {

    @Autowired
    private PowerTaskService powerTaskService;

    @ApiOperation(value = "请求转发")
    @GetMapping("/")
    public String index(){
        return "index";
    }

    @ApiOperation(value = "登陆请求转发")
    @GetMapping("login")
    public String login(){
        return "login";
    }

    @ApiOperation(value = "跳转前端测试页面")
    @GetMapping("webTest")
    public String webTest(){
        return "web-test";
    }

    @ApiOperation(value ="测试类")
    @PostMapping("data")
    @ResponseBody
    public Result receiveData(@ApiParam(name = "formData",value ="表单传过来的数据") @RequestParam String formData,
                              @ApiParam(name = "result返回值",value ="返回的结果值") @RequestParam Map<String,String> result){

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


    @ApiOperation(value = "获得用户任务信息")
    @GetMapping("taskInfo")
    @ResponseBody
    public Result userTaskInfo(@ApiParam(name = "assignee",value = "用户Id") @RequestParam String assignee
                              ){
         return powerTaskService.queryTaskList(assignee);
    }
}
