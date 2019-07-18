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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : xuyunfeng
 * @date :   2019/7/18 14:17
 */
@Controller
@RequestMapping("form")
public class FormController {
    @Autowired
    private FormService formService;

    @Autowired
    private TaskService taskService;

    @GetMapping("formData/{proDefId}")
    public ResponseEntity getStartFormData(@PathVariable String proDefId){

        StartFormData startFormData = formService.getStartFormData(proDefId);
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


    @GetMapping("submitStartFormData/{proDefId}")
    public ResponseEntity submitStartFormData(@PathVariable String proDefId){
        Map<String, String> vars=new HashMap<String, String>();
        vars.put("start_date",getDate());
        vars.put("end_date",getDate());
        vars.put("reason","我想出去玩玩");
        vars.put("days","3");
        formService.submitStartFormData(proDefId,vars);
        return ResponseEntity.ok(vars);
    }


    public  String getDate(){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        String s = simpleDateFormat.format(new Date());

        return  s;
    }


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
     * @param taskId
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
     * @param taskId
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
