package com.power.mapper;

import com.power.bean.PowerDeployment;
import com.power.bean.PowerProcdef;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author : xuyunfeng
 * @date :   2019/7/17 15:19
 */
@Mapper
public interface ProcessMapper {
    List<PowerDeployment> findProcessList();

    List<PowerProcdef> findProcdefList();

}
