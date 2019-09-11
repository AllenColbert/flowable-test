package com.power.service;

import com.power.util.Result;

/**
 * @author : xuyunfeng
 * @date :   2019/8/22 10:45
 */
public interface PowerHistoryService {

    /**
     * 查询我的历史任务
     * @param assignee 用户ID
     * @return 历史任务数据
     */
    Result findMyHistoryTask(String assignee);
}
