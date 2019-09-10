package com.power.service.impl;

import com.power.service.PowerHistoryService;
import com.power.util.Result;
import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricDetail;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskLogEntry;
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
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().listPage(1,10);
        List<HistoricDetail> historicDetails = historyService.createHistoricDetailQuery().listPage(1, 10);
        List<HistoricTaskLogEntry> historicTaskLogEntries = historyService.createHistoricTaskLogEntryQuery().listPage(1, 10);
        List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery().listPage(1, 10);
        List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery().listPage(1, 10);

        return Result.success();
    }

    @Override
    public Result findHistoryList(String processDefinitionId) {
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().processDefinitionId(processDefinitionId).list();
        list.forEach(historyTask ->
            System.out.println("历史任务名称"+historyTask.getName()+";任务发起时间："+historyTask.getStartTime())
        );
        return Result.success();
    }

    @Override
    public Result findMyHistoryTask(String assignee) {
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().taskAssignee(assignee).list();

        list.forEach(task -> System.out.println("workTime:"+task.getWorkTimeInMillis()));
        return Result.success();
    }
}
