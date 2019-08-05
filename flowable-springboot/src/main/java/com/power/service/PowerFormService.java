package com.power.service;

import org.flowable.engine.form.StartFormData;

/**
 * 流程表单Service
 * @author : xuyunfeng
 * @date :   2019/7/18 14:18
 */
public interface PowerFormService {

    /**
     * 获取流程表单信息
     * @param processDefinitionId 流程定义Id
     * @return xx
     */
    StartFormData getStartFormData(String processDefinitionId);
}
