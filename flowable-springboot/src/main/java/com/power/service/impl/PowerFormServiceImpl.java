package com.power.service.impl;

import com.power.service.PowerFormService;
import org.flowable.engine.FormService;
import org.flowable.engine.form.StartFormData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : xuyunfeng
 * @date :   2019/8/14 15:00
 */
@Service
public class PowerFormServiceImpl implements PowerFormService {
    @Autowired
    private FormService formService;


    @Override
    public StartFormData getStartFormData(String processDefinitionId) {

        return null;
    }
}
