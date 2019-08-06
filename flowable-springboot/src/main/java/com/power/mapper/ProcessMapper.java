package com.power.mapper;

import com.power.entity.PowerDeployment;
import com.power.entity.PowerProcessDefinition;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author : xuyunfeng
 * @date :   2019/7/17 15:19
 */
@Mapper
public interface ProcessMapper {
    /**
     * 查询流程部署列表
     * @return  List<PowerDeployment>
     */
    List<PowerDeployment> findProcessList();

    /**
     * 查询流程定义列表
     * @return List<PowerProcessDefinition>
     */
    List<PowerProcessDefinition> findProcdefList();

}
