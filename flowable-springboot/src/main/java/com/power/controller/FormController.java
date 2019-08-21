package com.power.controller;

import org.flowable.engine.FormService;
import org.flowable.engine.TaskService;
import org.flowable.engine.form.FormProperty;
import org.flowable.engine.form.FormType;
import org.flowable.engine.form.StartFormData;
import org.flowable.engine.form.TaskFormData;
import org.flowable.engine.impl.form.DateFormType;
import org.flowable.engine.impl.form.EnumFormType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : xuyunfeng
 * @date :   2019/7/18 14:17
 * 表单模块
 */
@ApiIgnore()
@Controller
@RequestMapping("form")
public class FormController {
    @Autowired
    private FormService formService;

    @Autowired
    private TaskService taskService;

    /**
     * 通过流程定义Id获取表单信息
     * @param processDefinitionId 流程定义Id
     * @return 表单信息列表
     */
    @GetMapping("formData/{processDefinitionId}")
    public ResponseEntity getStartFormData(@PathVariable String processDefinitionId){

        StartFormData startFormData = formService.getStartFormData(processDefinitionId);
        System.out.println("ProcessDefinition:"+startFormData.getProcessDefinition());
        System.out.println(startFormData.getDeploymentId());
        System.out.println(startFormData.getFormKey());

        List<FormProperty> formProperties = startFormData.getFormProperties();

        for (FormProperty fm : formProperties){
            System.out.println("############################");
            System.out.println(fm.getId());
            System.out.println(fm.getName());
            FormType formType = fm.getType();
            System.out.println(formType);
            String key="";
            //判断表单是枚举还是日期类型，通过key获取Information；
            if (formType instanceof EnumFormType){
                key="values";
            }else if (formType instanceof DateFormType){
                key="datePattern";
            }
            Object information = formType.getInformation(key);
            System.out.println("information:"+information);
            System.out.println(fm.getValue());
            System.out.println("############################");
        }

        return ResponseEntity.ok(formProperties);
    }

    /**
     * 提交自定义表单，通过流程定义ID进行绑定
     * @param processDefinitionId 流程定义ID
     * @return  自定义表单信息
     */
    @GetMapping("submitStartFormData/{processDefinitionId}")
    public ResponseEntity submitStartFormData(@PathVariable String processDefinitionId){
        Map<String, String> vars=new HashMap<String, String>();
        vars.put("start_date",getDate());
        vars.put("end_date",getDate());
        vars.put("reason","我想出去玩玩");
        vars.put("days","3");
        formService.submitStartFormData(processDefinitionId,vars);
        return ResponseEntity.ok(vars);
    }

    /**
     * 日期方法
     * @return 日期格式
     */
    public  String getDate(){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(new Date());
    }

    /**
     * 通过任务Id获取对应的表单信息
     * @param taskId 任务Id
     * @return 表单信息
     */
    @GetMapping("taskFormData/{taskId}")
    public ResponseEntity getTaskFormData(@PathVariable("taskId")String taskId){
        TaskFormData taskFormData = formService.getTaskFormData(taskId);
        List<FormProperty> formProperties = taskFormData.getFormProperties();

        for (FormProperty fm : formProperties){
            System.out.println("############################");
            System.out.println(fm.getId());
            String key="";
            System.out.println(fm.getName());
            FormType formType = fm.getType();
            System.out.println(formType);
            //判断表单是枚举还是日期类型，通过key获取Information；
            if (formType instanceof DateFormType){
                key="datePattern";
            }else if (formType instanceof EnumFormType){
                key="values";
            }
            Object information = formType.getInformation(key);
            System.out.println("information:"+information);
            System.out.println(fm.getValue());
            System.out.println("############################");
        }

        return ResponseEntity.ok(formProperties);
    }

    /**
     * 任务节点中填充值，不会自动完成当前任务节点
     * @param taskId 任务Id
     */
    @GetMapping("saveFormData/{taskId}")
    public void saveFormData(@PathVariable("taskId")String taskId) {
        Map<String, String> vars=new HashMap<String, String>();
        vars.put("分享牛1",getDate());
        vars.put("分享牛2",getDate());
        vars.put("reason","我想出去玩玩2222");
        vars.put("days","5");
        formService.saveFormData(taskId,vars);
    }

    /**
     * 填充值，并自动完成当前任务节点
     * @param taskId 任务Id
     */
    @GetMapping("submitTaskFormData/{taskId}")
    public void submitTaskFormData(@PathVariable("taskId")String taskId) {
        Map<String, String> vars=new HashMap<String, String>();
        vars.put("分享牛1",getDate());
        vars.put("分享牛2",getDate());
        vars.put("reason","我想出去玩玩2222");
        vars.put("days","5");
        formService.submitTaskFormData(taskId,vars);
    }


    @GetMapping("getStartFormKey/{proDefId}")
    public ResponseEntity getStartFormKey(@PathVariable("proDefId")String proDefId){
        String startFormKey = formService.getStartFormKey(proDefId);

        return ResponseEntity.ok("开始节点表单key："+startFormKey);
    }


    @GetMapping("getRenderedStartForm/{proDefId}")
    public ResponseEntity getRenderedStartForm(@PathVariable("proDefId")String proDefId){
        Object startFormKey = formService.getRenderedStartForm(proDefId);
        return ResponseEntity.ok("开始节点表单key："+startFormKey);
    }



}
