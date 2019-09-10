package com.power.service;

import com.power.util.Result;

/**
 * @author : xuyunfeng
 * @date :   2019/8/22 10:45
 */
public interface PowerHistoryService {

    Result findHistoricProcessInstance();

    Result findHistoryList(String processDefinitionId);

    Result findMyHistoryTask(String assignee);
}
