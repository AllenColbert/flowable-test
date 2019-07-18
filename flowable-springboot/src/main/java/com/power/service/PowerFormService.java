package com.power.service;

import org.flowable.engine.form.StartFormData;

/**
 * @author : xuyunfeng
 * @date :   2019/7/18 14:18
 */
public interface PowerFormService {
    StartFormData getStartFormData(String processDefinitionId);
}
