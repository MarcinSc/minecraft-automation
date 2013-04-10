package com.gempukku.minecraft.automation.lang;

public interface FunctionExecutable {
    public String[] getParameterNames();

    public CallContext getCallContext();

    public Execution createExecution(CallContext callContext);
}
