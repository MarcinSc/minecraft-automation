package com.gempukku.minecraft.automation.lang.execution;

import com.gempukku.minecraft.automation.lang.*;

public class NegateExecution implements Execution {
	private ExecutableStatement _expression;

	private boolean _stackedExpression;
	private boolean _assignedValue;

	public NegateExecution(ExecutableStatement expression) {
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
			if (contextValue.getType() != Variable.Type.BOOLEAN)
				throw new ExecutionException("Expected BOOLEAN");
			executionContext.setContextValue(new Variable(!(Boolean) contextValue.getValue()));
			return new ExecutionProgress(ExecutionTimes.GET_CONTEXT_VALUE + ExecutionTimes.SET_CONTEXT_VALUE);
		}
		return null;
	}
}
