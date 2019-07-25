package com.power.cmd;

import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.persistence.deploy.DeploymentManager;
import org.flowable.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import org.flowable.engine.impl.util.CommandContextUtil;

/**
 * @author : xuyunfeng
 * @date :   2019/7/25 15:41
 */
public class GetProcessDefinitionCacheEntryCmd implements Command<ProcessDefinitionCacheEntry> {

    private String processDefinitionId;

    public GetProcessDefinitionCacheEntryCmd(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    @Override
    public ProcessDefinitionCacheEntry execute(CommandContext commandContext) {
        DeploymentManager deploymentManager = CommandContextUtil
                .getProcessEngineConfiguration().getDeploymentManager();

        return deploymentManager.getProcessDefinitionCache().get(processDefinitionId);
    }
}
