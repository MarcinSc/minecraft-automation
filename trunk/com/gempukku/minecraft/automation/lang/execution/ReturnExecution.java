package com.gempukku.minecraft.automation.lang.execution;

import com.gempukku.minecraft.automation.lang.*;

public class ReturnExecution implements Execution {
    private ExecutableStatement _result;

    private boolean _stackedExecution;
    private boolean _returnedResult;

    public ReturnExecution(ExecutableStatement result) {
        _result = result;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (!_stackedExecution)
            return true;
        if (!_returnedResult)
            return true;
        return false;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext) throws ExecutionException {
        if (!_stackedExecution) {
            executionContext.stackExecution(_result.createExecution());
            _stackedExecution = true;
            return new ExecutionProgress(100);
        }
        if (!_returnedResult) {
            executionContext.setReturnValue(executionContext.getContextValue());
            _returnedResult = true;
            return new ExecutionProgress(100);
        }
        return null;
    }
}
