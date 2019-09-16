package com.power.service.impl;

import com.power.entity.PowerHistoricActivity;
import com.power.service.PowerHistoryService;
import com.power.util.Result;
import org.flowable.engine.HistoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.task.Comment;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : xuyunfeng
 * @date :   2019/8/22 10:45
 */
@Service
public class PowerHistoryServiceImpl implements PowerHistoryService {
    @Autowired
    private HistoryService historyService;

    @Autowired
    private TaskService taskService;

    @Override
    public Result findMyHistoryTask(String assignee){
        //分页
        int index = 0;
        int limit = 10;

        List<PowerHistoricActivity> powerHistoricActivities = new ArrayList<>();

        //根据用户名查询办理过的历史任务
        List<HistoricActivityInstance> historicActivity = historyService.createHistoricActivityInstanceQuery()
                .taskAssignee(assignee).orderByHistoricActivityInstanceEndTime().desc().listPage(index, limit);
        historicActivity.forEach(historicTask -> {
            String taskId = historicTask.getTaskId();
            List<Comment> taskComments = taskService.getTaskComments(taskId);
            PowerHistoricActivity powerHistoricActivity = new PowerHistoricActivity();
            //历史任务评论不为空时才添加
            if (taskComments.size() > 0){
                Comment taskComment = taskComments.get(0);
                powerHistoricActivity.setMessage(taskComment.getFullMessage());
            }
            BeanUtils.copyProperties(historicTask,powerHistoricActivity);
            powerHistoricActivities.add(powerHistoricActivity);

        });

        return Result.success(powerHistoricActivities);
    }


}
