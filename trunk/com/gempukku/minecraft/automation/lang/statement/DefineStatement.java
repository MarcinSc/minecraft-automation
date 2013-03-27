package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.execution.SimpleExecution;

public class DefineStatement implements ExecutableStatement {
    private String _name;

    public DefineStatement(String name) {
        _name = name;
    }

    @Override
    public Execution createExecution() {
        return new SimpleExecution() {
            @Override
            protected ExecutionProgress execute(ExecutionContext context) throws ExecutionException {
                context.peekCallContext().defineVariable(_name);
                return new ExecutionProgress(100);
            }
        };
    }
}
