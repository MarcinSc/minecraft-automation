package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.ExecutableStatement;
import com.gempukku.minecraft.automation.lang.Execution;
import com.gempukku.minecraft.automation.lang.execution.MethodCallExecution;

import java.util.List;

public class MethodCallStatement implements ExecutableStatement {
    private ExecutableStatement _objectMethod;
    private String _methodName;
    private List<ExecutableStatement> _parameters;

    public MethodCallStatement(ExecutableStatement objectMethod, String methodName, List<ExecutableStatement> parameters) {
        _objectMethod = objectMethod;
        _methodName = methodName;
        _parameters = parameters;
    }

    @Override
    public Execution createExecution() {
        return new MethodCallExecution(_objectMethod, _methodName, _parameters);
    }
}
