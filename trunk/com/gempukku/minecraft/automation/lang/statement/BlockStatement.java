package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.ExecutableStatement;
import com.gempukku.minecraft.automation.lang.Execution;
import com.gempukku.minecraft.automation.lang.execution.MultiStatementExecution;

import java.util.List;

public class BlockStatement implements ExecutableStatement {
    private List<ExecutableStatement> _statements;

    public BlockStatement(List<ExecutableStatement> statements) {
        _statements = statements;
    }

    public Execution createExecution() {
        return new MultiStatementExecution(_statements);
    }
}
