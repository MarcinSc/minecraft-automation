package com.gempukku.minecraft.automation.lang.execution;

import com.gempukku.minecraft.automation.lang.*;

public class IfExecution implements Execution {
    private ExecutableStatement _condition;
    private ExecutableStatement _statement;

    private boolean _conditionStacked;
    private boolean _statementExecutedIfNeeded;

    public IfExecution(ExecutableStatement condition, ExecutableStatement statement) {
        _condition = condition;
        _statement = statement;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (!_conditionStacked)
            return true;
        if (!_statementExecutedIfNeeded)
            return true;
        return false;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext) throws ExecutionException {
        if (!_conditionStacked) {
            executionContext.stackExecution(_condition.createExecution());
            _conditionStacked = true;
            return new ExecutionProgress(100);
        }
        if (!_statementExecutedIfNeeded) {
            final Variable value = executionContext.getContextValue();
            if (value.getType() != Variable.Type.BOOLEAN)
                throw new ExecutionException("Condition not of type BOOLEAN");
            boolean ifResult = (Boolean) value.getValue();
            if (ifResult)
                executionContext.stackExecution(_statement.createExecution());
            _statementExecutedIfNeeded = true;
            return new ExecutionProgress(100);
        }
        return null;
    }
}
