package com.power.cmd;

import org.flowable.bpmn.model.Process;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;

/**
 * 获取Process对象，因为需要上下文commandContext，所以只能通过Cmd的方法获取；
 * @author : xuyunfeng
 * @date :   2019/7/23 16:40
 */
public class GetProcessCmd implements Command<Process> {

    private String  processDefinitionId;

    public GetProcessCmd(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    @Override
    public Process execute(CommandContext commandContext) {
        return ProcessDefinitionUtil.getProcess(processDefinitionId);
    }
}
