package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.ExecutableStatement;
import com.gempukku.minecraft.automation.lang.Execution;
import com.gempukku.minecraft.automation.lang.execution.WhileExecution;

public class WhileStatement implements ExecutableStatement {
    private ExecutableStatement _condition;
    private ExecutableStatement _statement;

    public WhileStatement(ExecutableStatement condition, ExecutableStatement statement) {
        _condition = condition;
        _statement = statement;
    }

    @Override
    public Execution createExecution() {
        return new WhileExecution(_condition, _statement);
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
