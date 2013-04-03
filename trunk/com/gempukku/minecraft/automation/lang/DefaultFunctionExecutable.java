package com.gempukku.minecraft.automation.lang;

public class DefaultFunctionExecutable implements FunctionExecutable {
    private ExecutableStatement _statement;
    private String[] _parameterNames;

    public DefaultFunctionExecutable(String[] parameterNames) {
        _parameterNames = parameterNames;
    }

    public void setStatement(ExecutableStatement statement) {
        _statement = statement;
    }

    public String[] getParameterNames() {
        return _parameterNames;
    }

    public Execution createExecution(CallContext context) {
        return _statement.createExecution();
    }
}
