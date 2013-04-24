package com.gempukku.minecraft.automation.lang.execution;

import com.gempukku.minecraft.automation.lang.*;

public class MathExecution implements Execution {
	private ExecutableStatement _left;
	private Operator _operator;
	private ExecutableStatement _right;
	private boolean _assignToLeft;

	private boolean _stackedLeft;
	private boolean _resolvedLeft;
	private boolean _stackedRight;
	private boolean _resolvedAndAssignedSum;

	private Variable _leftValue;

	public MathExecution(ExecutableStatement left, Operator operator, ExecutableStatement right, boolean assignToLeft) {
		_left = left;
		_operator = operator;
		_right = right;
		_assignToLeft = assignToLeft;
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
			return new ExecutionProgress(ExecutionTimes.STACK_EXECUTION);
		}
		if (!_resolvedLeft) {
			_leftValue = executionContext.getContextValue();
			_resolvedLeft = true;
			return new ExecutionProgress(ExecutionTimes.GET_CONTEXT_VALUE);
		}
		if (!_stackedRight) {
			executionContext.stackExecution(_right.createExecution());
			_stackedRight = true;
			return new ExecutionProgress(ExecutionTimes.STACK_EXECUTION);
		}
		if (!_resolvedAndAssignedSum) {
			Variable rightValue = executionContext.getContextValue();
			if (rightValue.getType() == Variable.Type.NUMBER && _leftValue.getType() == Variable.Type.NUMBER) {
				final float valueLeft = ((Number) _leftValue.getValue()).floatValue();
				final float valueRight = ((Number) rightValue.getValue()).floatValue();
				Object result;
				if (_operator == Operator.SUBTRACT || _operator == Operator.SUBTRACT_ASSIGN)
					result = valueLeft - valueRight;
				else if (_operator == Operator.DIVIDE || _operator == Operator.DIVIDE_ASSIGN)
					result = valueLeft / valueRight;
				else if (_operator == Operator.MULTIPLY || _operator == Operator.MULTIPLY_ASSIGN)
					result = valueLeft * valueRight;
				else if (_operator == Operator.MOD || _operator == Operator.MOD_ASSIGN)
					result = valueLeft % valueRight;
				else if (_operator == Operator.GREATER_OR_EQUAL)
					result = valueLeft >= valueRight;
				else if (_operator == Operator.GREATER)
					result = valueLeft > valueRight;
				else if (_operator == Operator.LESS_OR_EQUAL)
					result = valueLeft <= valueRight;
				else if (_operator == Operator.LESS)
					result = valueLeft < valueRight;
				else
					throw new ExecutionException("Unknown operator " + _operator);

				if (_assignToLeft)
					_leftValue.setValue(result);
				executionContext.setContextValue(new Variable(result));
			} else {
				throw new ExecutionException("Unable to perform mathematical operation on two non-number values " + _leftValue.getType() + " and " + rightValue.getType());
			}
			_resolvedAndAssignedSum = true;
			return new ExecutionProgress(ExecutionTimes.GET_CONTEXT_VALUE + ExecutionTimes.OTHER_MATH_OPERATION + ExecutionTimes.SET_CONTEXT_VALUE);
		}
		return null;
	}

}
