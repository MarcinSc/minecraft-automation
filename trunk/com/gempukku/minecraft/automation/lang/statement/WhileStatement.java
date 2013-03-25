package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.ExecutableStatement;
import com.gempukku.minecraft.automation.lang.Execution;
import com.gempukku.minecraft.automation.lang.execution.WhileExecution;

import java.util.List;

public class WhileStatement implements ExecutableStatement {
    private ExecutableStatement _condition;
    private List<ExecutableStatement> _statements;

    public WhileStatement(ExecutableStatement condition, List<ExecutableStatement> statements) {
        _condition = condition;
        _statements = statements;
    }

    @Override
    public Execution createExecution() {
        return new WhileExecution(_condition, _statements);
    }
}
