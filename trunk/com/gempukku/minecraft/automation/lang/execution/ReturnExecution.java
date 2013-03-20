package com.gempukku.minecraft.automation.lang.execution;

import com.gempukku.minecraft.automation.lang.*;

public class ReturnExecution implements Execution {
    private ExecutableStatement _value;

    private boolean _stackedValueCall;
    private boolean _assignedReturnValue;

    public ReturnExecution(ExecutableStatement value) {
        _value = value;
    }

    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (!_stackedValueCall)
            return true;
        if (!_assignedReturnValue)
            return true;
        return false;
    }

    public ExecutionProgress executeNextStatement(ExecutionContext executionContext) throws IllegalSyntaxException {
        if (!_stackedValueCall) {
            executionContext.stackExecution(_value.createExecution());
            _stackedValueCall = true;
            return new ExecutionProgress(100);
        }
        if (!_assignedReturnValue) {
            executionContext.peekCallContext().setReturnValue(executionContext.getContextValue());
            _assignedReturnValue = true;
            return new ExecutionProgress(100);
        }
        throw new IllegalStateException("Should not get here");
    }
}
