package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.execution.SimpleExecution;

public class ConstantStatement implements ExecutableStatement {
    private Variable _value;

    public ConstantStatement(Variable value) {
        _value = value;
    }

    public Execution createExecution() {
        return new SimpleExecution() {
            @Override
            protected ExecutionProgress execute(ExecutionContext context) {
                context.setContextValue(_value);
                return new ExecutionProgress(100);
            }
        };
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
