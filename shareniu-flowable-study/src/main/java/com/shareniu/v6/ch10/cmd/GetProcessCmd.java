package com.shareniu.v6.ch10.cmd;

import org.flowable.bpmn.model.Process;
import org.flowable.engine.common.impl.interceptor.Command;
import org.flowable.engine.common.impl.interceptor.CommandContext;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
/**
 * 
 * 获取Process信息
 *
 */
public class GetProcessCmd  implements Command<Process>{
	protected String processDefinitionId;
	
	public GetProcessCmd(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	@Override
	public Process execute(CommandContext commandContext) {
		Process process = ProcessDefinitionUtil.getProcess(processDefinitionId);
		return process;
	}

}
