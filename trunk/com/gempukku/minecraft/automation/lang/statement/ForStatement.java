package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.ExecutableStatement;
import com.gempukku.minecraft.automation.lang.Execution;
import com.gempukku.minecraft.automation.lang.execution.ForExecution;

public class ForStatement implements ExecutableStatement {
    private ExecutableStatement _initializationStatement;
    private ExecutableStatement _terminationCondition;
    private ExecutableStatement _executedAfterEachLoop;
    private ExecutableStatement _statementInLoop;

    public ForStatement(ExecutableStatement initializationStatement, ExecutableStatement terminationCondition, ExecutableStatement executedAfterEachLoop, ExecutableStatement statementInLoop) {
        _initializationStatement = initializationStatement;
        _terminationCondition = terminationCondition;
        _executedAfterEachLoop = executedAfterEachLoop;
        _statementInLoop = statementInLoop;
    }

    @Override
    public Execution createExecution() {
        return new ForExecution(_initializationStatement, _terminationCondition, _executedAfterEachLoop, _statementInLoop);
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
