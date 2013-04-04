package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.execution.SimpleExecution;

public class BreakStatement implements ExecutableStatement {
    @Override
    public Execution createExecution() {
        return new SimpleExecution() {
            @Override
            protected ExecutionProgress execute(ExecutionContext context) throws ExecutionException {
                context.breakBlock();
                return new ExecutionProgress(100);
            }
        };
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
