package com.gempukku.minecraft.automation.lang.execution;

import com.gempukku.minecraft.automation.lang.Execution;
import com.gempukku.minecraft.automation.lang.ExecutionContext;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.ExecutionProgress;

public class DefineFunctionExecution implements Execution {
    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        return false;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext) throws ExecutionException {
        return null;
    }
}
