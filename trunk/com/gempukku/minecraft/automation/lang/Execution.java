package com.gempukku.minecraft.automation.lang;

public interface Execution {
    public boolean hasNextExecution(ExecutionContext executionContext);
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext) throws IllegalSyntaxException;
}
