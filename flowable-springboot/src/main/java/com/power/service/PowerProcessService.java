package com.power.service;


import com.power.bean.PowerDeployment;
import com.power.bean.PowerProcdef;
import com.power.bean.PowerDeployEntity;

import java.util.List;


/**
 * @author xuyunfeng
 * @date 2019/7/9 14:20
 */
public interface PowerProcessService {

    /**
     * @param fileName 文件名称
     * @param processDeploy
     * @return
     */
    Object deployProcess(String fileName, PowerDeployEntity processDeploy);

    /**
     * @return  显示流程定义列表
     */
    List<PowerDeployment> findProcessList();


    List<PowerProcdef> findProcdefList();
}
