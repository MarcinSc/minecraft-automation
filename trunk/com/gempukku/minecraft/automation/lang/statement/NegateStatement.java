package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.ExecutableStatement;
import com.gempukku.minecraft.automation.lang.Execution;
import com.gempukku.minecraft.automation.lang.execution.NegateExecution;

public class NegateStatement implements ExecutableStatement {
    private ExecutableStatement _expression;

    public NegateStatement(ExecutableStatement expression) {
        _expression = expression;
    }

    @Override
    public Execution createExecution() {
        return new NegateExecution(_expression);
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
