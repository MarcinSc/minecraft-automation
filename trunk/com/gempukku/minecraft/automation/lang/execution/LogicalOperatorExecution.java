package com.gempukku.minecraft.automation.lang.execution;

import com.gempukku.minecraft.automation.lang.*;

public class LogicalOperatorExecution implements Execution {
    private ExecutableStatement _left;
    private Operator _operator;
    private ExecutableStatement _right;

    private boolean _terminated;

    private boolean _stackedLeft;
    private boolean _resolvedLeft;

    private boolean _stackedRight;
    private boolean _resolvedRight;

    public LogicalOperatorExecution(ExecutableStatement left, Operator operator, ExecutableStatement right) {
        _left = left;
        _operator = operator;
        _right = right;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (_terminated)
            return false;
        return true;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext) throws ExecutionException {
        if (!_stackedLeft) {
            _stackedLeft = true;
            executionContext.stackExecution(_left.createExecution());
            return new ExecutionProgress(100);
        }
        if (!_resolvedLeft) {
            _resolvedLeft = true;
            final Variable contextValue = executionContext.getContextValue();
            if (contextValue.getType() != Variable.Type.BOOLEAN)
                throw new ExecutionException("Expected BOOLEAN");
            boolean result = (Boolean) contextValue.getValue();
            if (_operator == Operator.AND && !result) {
                _terminated = true;
                executionContext.setContextValue(new Variable(false));
            } else if (_operator == Operator.OR && result) {
                _terminated = true;
                executionContext.setContextValue(new Variable(true));
            }
            return new ExecutionProgress(100);
        }
        if (!_stackedRight) {
            _stackedRight = true;
            executionContext.stackExecution(_right.createExecution());
            return new ExecutionProgress(100);
        }
        if (!_resolvedRight) {
            _resolvedRight = true;
            final Variable contextValue = executionContext.getContextValue();
            if (contextValue.getType() != Variable.Type.BOOLEAN)
                throw new ExecutionException("Expected BOOLEAN");
            _terminated = true;
            boolean result = (Boolean) contextValue.getValue();
            executionContext.setContextValue(new Variable(result));
            return new ExecutionProgress(100);
        }
        return null;
    }
}
