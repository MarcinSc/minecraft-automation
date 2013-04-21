package com.gempukku.minecraft.automation.lang.execution;

import com.gempukku.minecraft.automation.lang.*;

public class WhileExecution implements Execution {
	private ExecutableStatement _condition;
	private ExecutableStatement _statement;

	private boolean _terminated;

	private boolean _conditionStacked;

	public WhileExecution(ExecutableStatement condition, ExecutableStatement statement) {
		_condition = condition;
		_statement = statement;
	}

	@Override
	public boolean hasNextExecution(ExecutionContext executionContext) {
		if (_terminated)
			return false;

		return true;
	}

	@Override
	public ExecutionProgress executeNextStatement(ExecutionContext executionContext) throws ExecutionException {
		if (!_conditionStacked) {
			_conditionStacked = true;
			executionContext.stackExecution(_condition.createExecution());
			return new ExecutionProgress(ExecutionTimes.STACK_EXECUTION);
		}
		final Variable value = executionContext.getContextValue();
		if (value.getType() != Variable.Type.BOOLEAN)
			throw new ExecutionException("Condition not of type BOOLEAN");
		final Boolean result = (Boolean) value.getValue();
		if (!result)
			_terminated = true;
		else {
			executionContext.stackExecution(_statement.createExecution());
			_conditionStacked = false;
		}
		if (_terminated)
			return new ExecutionProgress(ExecutionTimes.COMPARE_VALUES + ExecutionTimes.STACK_EXECUTION);
		else
			return new ExecutionProgress(ExecutionTimes.STACK_EXECUTION);
	}
}
