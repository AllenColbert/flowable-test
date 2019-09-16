package com.power.service.impl;

import com.power.service.ConstructorService;
import org.flowable.engine.*;
import org.flowable.idm.api.IdmIdentityService;
import org.flowable.idm.api.IdmManagementService;

/**
 * @author : xuyunfeng
 * @date :   2019/8/23 10:29
 */
@Deprecated
public class  ConstructorServiceImpl implements ConstructorService {

    protected FormService formService;
    protected TaskService taskService;
    protected ProcessEngine processEngine;
    protected HistoryService historyService;
    protected IdmIdentityService idmIdentityService;
    protected IdentityService identityService;
    protected IdmManagementService idmManagementService;
    protected ManagementService managementService;
    protected DynamicBpmnService dynamicBpmnService;


}
