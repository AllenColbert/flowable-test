package com.power.mapper;

import com.power.entity.PowerTask;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author : xuyunfeng
 * @date :   2019/7/19 16:18
 */
@Mapper
public interface TaskMapper {
    List<PowerTask> queryUserTask(String assignee);
}
