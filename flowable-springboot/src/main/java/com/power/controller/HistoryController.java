package com.power.controller;

import com.power.service.PowerHistoryService;
import com.power.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author : xuyunfeng
 * @date :   2019/8/22 10:49
 */
@Api(value = "历史任务查询接口",tags = "历史查询接口")
@Controller
@RequestMapping("history")
public class HistoryController {
    @Autowired
    private PowerHistoryService powerHistoryService;


    @ApiOperation(value = "查询我的历史任务")
    @GetMapping("myHistoryTask")
    @ResponseBody
    public Result myHistoryTask(@ApiParam(name = "assignee",value ="任务办理人") @RequestParam String assignee){
        return powerHistoryService.findMyHistoryTask(assignee);
    }



}
