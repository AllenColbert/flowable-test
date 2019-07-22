package com.shareniu.v6.ch5;

import org.flowable.bpmn.model.Process;
import org.flowable.engine.common.impl.interceptor.Command;
import org.flowable.engine.common.impl.interceptor.CommandContext;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
/**
 * 根据流程定义的id获取Process实例对象
 * @author jz
 *
 */
public class GetProcessCmd  implements Command<org.flowable.bpmn.model.Process>{
	
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
