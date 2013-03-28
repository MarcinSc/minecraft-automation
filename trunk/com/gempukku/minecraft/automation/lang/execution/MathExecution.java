package com.gempukku.minecraft.automation.lang.execution;

import com.gempukku.minecraft.automation.lang.*;

public class MathExecution implements Execution {
    private ExecutableStatement _left;
    private Operator _operator;
    private ExecutableStatement _right;

    private boolean _stackedLeft;
    private boolean _resolvedLeft;
    private boolean _stackedRight;
    private boolean _resolvedAndAssignedSum;

    private Variable _leftValue;

    public MathExecution(ExecutableStatement left, Operator operator, ExecutableStatement right) {
        _left = left;
        _operator = operator;
        _right = right;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (!_stackedLeft)
            return true;
        if (!_resolvedLeft)
            return true;
        if (!_stackedRight)
            return true;
        if (!_resolvedAndAssignedSum)
            return true;
        return false;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext) throws ExecutionException {
        if (!_stackedLeft) {
            executionContext.stackExecution(_left.createExecution());
            _stackedLeft = true;
            return new ExecutionProgress(100);
        }
        if (!_resolvedLeft) {
            _leftValue = executionContext.getContextValue();
            _resolvedLeft = true;
            return new ExecutionProgress(100);
        }
        if (!_stackedRight) {
            executionContext.stackExecution(_right.createExecution());
            _stackedRight = true;
            return new ExecutionProgress(100);
        }
        if (!_resolvedAndAssignedSum) {
            Variable rightValue = executionContext.getContextValue();
            if (rightValue.getType() == Variable.Type.NUMBER && _leftValue.getType() == Variable.Type.NUMBER) {
                final float valueLeft = ((Number) _leftValue.getValue()).floatValue();
                final float valueRight = ((Number) rightValue.getValue()).floatValue();
                float result;
                if (_operator == Operator.SUBTRACT)
                    result = valueLeft - valueRight;
                else if (_operator == Operator.DIVIDE)
                    result = valueLeft / valueRight;
                else if (_operator == Operator.MULTIPLY)
                    result = valueLeft * valueRight;
                else if (_operator == Operator.MOD)
                    result = valueLeft % valueRight;
                else
                    throw new ExecutionException("Unknown operator "+_operator);

                executionContext.setContextValue(new Variable(result));
            } else {
                throw new ExecutionException("Unable to perform mathematical operation on two non-number values " + _leftValue.getType() + " and " + rightValue.getType());
            }
            _resolvedAndAssignedSum = true;
            return new ExecutionProgress(100);
        }
        return null;
    }

}
