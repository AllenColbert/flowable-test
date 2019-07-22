package com.shareniu.shareniu_flowable_study.bpmn.ch4.core;

import org.flowable.bpmn.model.Process;
import org.flowable.engine.common.impl.interceptor.Command;
import org.flowable.engine.common.impl.interceptor.CommandContext;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;
/**
 * 获取Process 命令类
 * @author jz
 *
 */
public class GetProcessCmd implements Command<Process> {
	protected String processDefinitionId;

	public GetProcessCmd() {
	}

	public GetProcessCmd(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	@Override
	public Process execute(CommandContext commandContext) {
		Process process = ProcessDefinitionUtil.getProcess(processDefinitionId);
		return process;
	}

}
