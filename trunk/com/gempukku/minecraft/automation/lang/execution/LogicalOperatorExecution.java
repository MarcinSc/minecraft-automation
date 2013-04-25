package com.gempukku.minecraft.automation.lang.execution;

import com.gempukku.minecraft.automation.lang.*;

public class LogicalOperatorExecution implements Execution {
	private int _line;
	private ExecutableStatement _left;
	private Operator _operator;
	private ExecutableStatement _right;

	private boolean _terminated;

	private boolean _stackedLeft;
	private boolean _resolvedLeft;

	private boolean _stackedRight;
	private boolean _resolvedRight;

	public LogicalOperatorExecution(int line, ExecutableStatement left, Operator operator, ExecutableStatement right) {
		_line = line;
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
			return new ExecutionProgress(ExecutionTimes.STACK_EXECUTION);
		}
		if (!_resolvedLeft) {
			_resolvedLeft = true;
			final Variable contextValue = executionContext.getContextValue();
			if (contextValue.getType() != Variable.Type.BOOLEAN)
				throw new ExecutionException(_line, "Expected BOOLEAN");
			boolean result = (Boolean) contextValue.getValue();
			if (_operator == Operator.AND && !result) {
				_terminated = true;
				executionContext.setContextValue(new Variable(false));
			} else if (_operator == Operator.OR && result) {
				_terminated = true;
				executionContext.setContextValue(new Variable(true));
			}
			if (_terminated)
				return new ExecutionProgress(ExecutionTimes.GET_CONTEXT_VALUE + ExecutionTimes.SET_CONTEXT_VALUE);
			else
				return new ExecutionProgress(ExecutionTimes.GET_CONTEXT_VALUE);
		}
		if (!_stackedRight) {
			_stackedRight = true;
			executionContext.stackExecution(_right.createExecution());
			return new ExecutionProgress(ExecutionTimes.STACK_EXECUTION);
		}
		if (!_resolvedRight) {
			_resolvedRight = true;
			final Variable contextValue = executionContext.getContextValue();
			if (contextValue.getType() != Variable.Type.BOOLEAN)
				throw new ExecutionException(_line, "Expected BOOLEAN");
			_terminated = true;
			boolean result = (Boolean) contextValue.getValue();
			executionContext.setContextValue(new Variable(result));
			return new ExecutionProgress(ExecutionTimes.GET_CONTEXT_VALUE + ExecutionTimes.SET_CONTEXT_VALUE);
		}
		return null;
	}
}
