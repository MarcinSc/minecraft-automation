package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.ExecutableStatement;
import com.gempukku.minecraft.automation.lang.Execution;
import com.gempukku.minecraft.automation.lang.execution.IfExecution;

public class IfStatement implements ExecutableStatement {
    private ExecutableStatement _condition;
    private ExecutableStatement _statement;

    public IfStatement(ExecutableStatement condition, ExecutableStatement statement) {
        _condition = condition;
        _statement = statement;
    }

    @Override
    public Execution createExecution() {
        return new IfExecution(_condition, _statement);
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
