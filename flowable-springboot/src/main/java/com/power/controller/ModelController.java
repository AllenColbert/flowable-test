package com.power.controller;

import com.alibaba.fastjson.JSON;
import com.power.entity.PowerUserTaskEntity;
import com.power.service.PowerModelService;
import com.power.util.Result;
import com.power.util.ResultCode;
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
 * model控制
 * @author : xuyunfeng
 * @date :   2019/8/9 13:05
 */
@Api(value = "流程模型接口",tags = {"流程模型接口"})
@Controller
@RequestMapping("model")
public class ModelController {

    @Autowired
    private PowerModelService powerModelService;

    @ApiOperation(value = "任意流增加单节点操作")
    @GetMapping("addSingleNode")
    @ResponseBody
    public Result addSingleNode(@ApiParam(name = "processDefinitionId",value ="流程定义Id") @RequestParam String processDefinitionId,
                                @ApiParam(name = "userTaskData",value ="任务节点信息") @RequestParam String userTaskData){
        PowerUserTaskEntity userTaskEntity = null;
        try {
            userTaskEntity = JSON.parseObject(userTaskData, PowerUserTaskEntity.class);
        } catch (Exception e) {
            return Result.failure(ResultCode.MODEL_DATA_WRONG_WARNING);
        }
        return powerModelService.addSingleNode(processDefinitionId,userTaskEntity);
    }

    @ApiOperation(value = "查看用户任务节点信息")
    @GetMapping("userTaskView")
    @ResponseBody
    public Result userTaskView(@ApiParam(name = "processDefinitionId",value ="流程定义Id") @RequestParam String processDefinitionId,
                               @ApiParam(name = "activityId",value ="目标节点Id") @RequestParam String activityId){
        return powerModelService.userTaskView(processDefinitionId,activityId);
    }

}
