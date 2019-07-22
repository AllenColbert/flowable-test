package com.shareniu.v6.ch10.cmd;

import org.flowable.engine.common.impl.interceptor.Command;
import org.flowable.engine.common.impl.interceptor.CommandContext;
import org.flowable.engine.impl.persistence.deploy.DeploymentManager;
import org.flowable.engine.impl.persistence.deploy.ProcessDefinitionCacheEntry;
import org.flowable.engine.impl.util.CommandContextUtil;
/**
 * 
 *从缓存中获取ProcessDefinitionCacheEntry
 */
public class GetProcessDefinitionCacheEntryCmd implements Command<ProcessDefinitionCacheEntry> {
	protected String processDefinitionId;

	public GetProcessDefinitionCacheEntryCmd(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	@Override
	public ProcessDefinitionCacheEntry execute(CommandContext commandContext) {
		DeploymentManager deploymentManager = CommandContextUtil.getProcessEngineConfiguration().getDeploymentManager();
		ProcessDefinitionCacheEntry processDefinitionCacheEntry = deploymentManager.getProcessDefinitionCache()
				.get(processDefinitionId);
		return processDefinitionCacheEntry;
	}

}
