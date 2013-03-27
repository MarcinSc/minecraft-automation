package com.gempukku.minecraft.automation.lang.execution;

import com.gempukku.minecraft.automation.lang.*;

public class AssignExecution implements Execution {
    private boolean _define;
    private String _name;
    private ExecutableStatement _value;

    private boolean _stackedStatement;
    private boolean _assignedValue;

    public AssignExecution(boolean define, String name, ExecutableStatement value) {
        _define = define;
        _name = name;
        _value = value;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (!_stackedStatement)
            return true;
        if (!_assignedValue)
            return true;
        return false;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext) throws ExecutionException {
        if (!_stackedStatement) {
            executionContext.stackExecution(_value.createExecution());
            _stackedStatement = true;
            return new ExecutionProgress(100);
        }
        if (!_assignedValue) {
            final CallContext callContext = executionContext.peekCallContext();
            if (_define)
                callContext.defineVariable(_name);
            callContext.setVariableValue(_name, executionContext.getContextValue().getValue());
            _assignedValue = true;
            return new ExecutionProgress(100);
        }
        return null;
    }
}
