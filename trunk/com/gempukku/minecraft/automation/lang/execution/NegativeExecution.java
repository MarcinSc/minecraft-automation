package com.gempukku.minecraft.automation.lang.execution;

import com.gempukku.minecraft.automation.lang.*;

public class NegativeExecution implements Execution {
	private int _line;
	private ExecutableStatement _expression;

	private boolean _stackedExpression;
	private boolean _assignedValue;

	public NegativeExecution(int line, ExecutableStatement expression) {
		_line = line;
		_expression = expression;
	}

	@Override
	public boolean hasNextExecution(ExecutionContext executionContext) {
		if (!_stackedExpression)
			return true;
		if (!_assignedValue)
			return true;
		return false;
	}

	@Override
	public ExecutionProgress executeNextStatement(ExecutionContext executionContext) throws ExecutionException {
		if (!_stackedExpression) {
			_stackedExpression = true;
			executionContext.stackExecution(_expression.createExecution());
			return new ExecutionProgress(ExecutionTimes.STACK_EXECUTION);
		}
		if (!_assignedValue) {
			_assignedValue = true;
			final Variable contextValue = executionContext.getContextValue();
			if (contextValue.getType() != Variable.Type.NUMBER)
				throw new ExecutionException(_line, "Expected NUMBER");
			executionContext.setContextValue(new Variable(-((Number) contextValue.getValue()).floatValue()));
			return new ExecutionProgress(ExecutionTimes.GET_CONTEXT_VALUE + ExecutionTimes.SET_CONTEXT_VALUE);
		}
		return null;
	}
}
