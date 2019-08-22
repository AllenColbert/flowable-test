package com.power.service.impl;

import com.power.service.PowerHistoryService;
import com.power.util.Result;
import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : xuyunfeng
 * @date :   2019/8/22 10:45
 */
@Service
public class PowerHistoryServiceImpl implements PowerHistoryService {
    @Autowired
    private HistoryService historyService;

    @Override
    public Result findHistoricProcessInstance(){
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().finished().list();
        return Result.success(list);
    }
}
