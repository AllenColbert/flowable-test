package com.power.mapper;

import com.power.entity.PowerTask;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : xuyunfeng
 * @date :   2019/7/19 16:18
 */
@Mapper
public interface TaskMapper {
    /**
     * 根据用户id查询用户任务列表
     * @param assignee 用户id
     * @return List<PowerTask>
     */
    List<PowerTask> queryUserTask(String assignee);
}
