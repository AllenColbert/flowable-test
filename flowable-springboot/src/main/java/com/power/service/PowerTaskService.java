package com.power.service;

import java.util.Map;

/**
 * @author : xuyunfeng
 * @date :   2019/7/17 16:37
 */
public interface PowerTaskService {
    Object queryUserTask(String assignee);

    Object completeTask(String taskId, Map<String, Object> vars);
}
