package com.gempukku.minecraft.automation.lang.execution;

import com.gempukku.minecraft.automation.lang.*;

public class ForExecution implements Execution {
    private ExecutableStatement _initializationStatement;
    private ExecutableStatement _terminationCondition;
    private ExecutableStatement _executedAfterEachLoop;
    private ExecutableStatement _statementInLoop;

    private boolean _terminated;

    private boolean _stackedItself;
    private boolean _initialized;
    private boolean _conditionStacked;
    private boolean _conditionChecked;

    private boolean _statementStacked;

    public ForExecution(ExecutableStatement initializationStatement, ExecutableStatement terminationCondition, ExecutableStatement executedAfterEachLoop, ExecutableStatement statementInLoop) {
        _initializationStatement = initializationStatement;
        _terminationCondition = terminationCondition;
        _executedAfterEachLoop = executedAfterEachLoop;
        _statementInLoop = statementInLoop;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (_terminated)
            return false;

        return true;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext) throws ExecutionException {
        if (!_stackedItself) {
            _stackedItself = true;
            final CallContext currentContext = executionContext.peekCallContext();
            CallContext childContext = new CallContext(currentContext, false, false);
            executionContext.stackExecutionGroup(childContext, this);
            return new ExecutionProgress(100);
        }
        if (!_initialized) {
            _initialized = true;
            if (_initializationStatement != null) {
                executionContext.stackExecution(_initializationStatement.createExecution());
                return new ExecutionProgress(100);
            }
        }
        if (!_conditionStacked) {
            executionContext.stackExecution(_terminationCondition.createExecution());
            _conditionStacked = true;
            return new ExecutionProgress(100);
        }
        if (!_conditionChecked) {
            final Variable value = executionContext.getContextValue();
            if (value.getType() != Variable.Type.BOOLEAN)
                throw new ExecutionException("Condition not of type BOOLEAN");
            if (!(Boolean) value.getValue())
                _terminated = true;
            _conditionChecked = true;
            return new ExecutionProgress(100);
        }
        if (!_statementStacked) {
            _statementStacked = true;
            if (_statementInLoop != null) {
                executionContext.stackExecution(_statementInLoop.createExecution());
                return new ExecutionProgress(100);
            }
        }
        if (_executedAfterEachLoop != null)
            executionContext.stackExecution(_executedAfterEachLoop.createExecution());
        _conditionStacked = false;
        _conditionChecked = false;
        _statementStacked = false;
        return new ExecutionProgress(100);
    }
}
