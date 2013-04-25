package com.gempukku.minecraft.automation.lang;

public interface FunctionExecutable {
	public String[] getParameterNames();

	public CallContext getCallContext();

	public Execution createExecution(int line, ExecutionContext executionContext, CallContext callContext);
}
