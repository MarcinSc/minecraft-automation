package com.gempukku.minecraft.automation.lang;

public interface FunctionExecutable {
    public String[] getParameterNames();
    public Execution createExecution(CallContext callContext);
}
