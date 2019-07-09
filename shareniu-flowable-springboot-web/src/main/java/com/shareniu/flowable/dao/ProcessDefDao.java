package com.shareniu.flowable.dao;

import com.shareniu.flowable.entity.ProcessDefEntity;
import com.shareniu.flowable.util.Parametermap;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Administrator on 2018/1/3.
 */
@Component
public interface ProcessDefDao {
    /**
     * 查询所有已经部署的流程
     * @param pm
     * @return
     */
    public List<ProcessDefEntity> queryPageAllProcessDefPage(Parametermap pm);

}
