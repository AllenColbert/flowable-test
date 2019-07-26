package com.power.mapper;

import com.power.entity.PowerDeployment;
import com.power.entity.PowerProcessDefinition;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author : xuyunfeng
 * @date :   2019/7/17 15:19
 */
@Mapper
public interface ProcessMapper {
    List<PowerDeployment> findProcessList();

    List<PowerProcessDefinition> findProcdefList();

}
