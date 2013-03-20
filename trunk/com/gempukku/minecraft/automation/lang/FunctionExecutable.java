package com.gempukku.minecraft.automation.lang;

public class FunctionExecutable extends ScriptExecutable {
    private String[] _parameterNames;

    public FunctionExecutable(String[] parameterNames) {
        _parameterNames = parameterNames;
    }

    public String[] getParameterNames() {
        return _parameterNames;
    }
}
