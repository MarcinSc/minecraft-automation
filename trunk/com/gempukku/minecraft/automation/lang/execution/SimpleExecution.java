package com.gempukku.minecraft.automation.lang.execution;

import com.gempukku.minecraft.automation.lang.Execution;
import com.gempukku.minecraft.automation.lang.ExecutionContext;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.ExecutionProgress;

public abstract class SimpleExecution implements Execution {
    private boolean _executed;

    public boolean hasNextExecution(ExecutionContext executionContext) {
        return !_executed;
    }

    public ExecutionProgress executeNextStatement(ExecutionContext executionContext) throws ExecutionException {
        final ExecutionProgress result = execute(executionContext);
        _executed = true;
        return result;
    }

    protected abstract ExecutionProgress execute(ExecutionContext context) throws ExecutionException;
}
