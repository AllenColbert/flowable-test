package com.power.mapper;

import com.power.entity.PowerTask;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : xuyunfeng
 * @date :   2019/7/19 16:18
 */
@Mapper
public interface TaskMapper {
    PowerTask queryUserTask(String assignee);
}
