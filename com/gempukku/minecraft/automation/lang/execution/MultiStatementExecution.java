package com.gempukku.minecraft.automation.lang.execution;

import com.gempukku.minecraft.automation.lang.*;

import java.util.List;

public class MultiStatementExecution implements Execution {
	private List<ExecutableStatement> _statements;
	private int _nextIndex = 0;

	public MultiStatementExecution(List<ExecutableStatement> statements) {
		_statements = statements;
	}

	public boolean hasNextExecution(ExecutionContext executionContext) {
		return _nextIndex < _statements.size();
	}

	public ExecutionProgress executeNextStatement(ExecutionContext executionContext) {
		final ExecutableStatement executableStatement = _statements.get(_nextIndex);
		Execution execution = executableStatement.createExecution();
		executionContext.stackExecution(execution);
		_nextIndex++;
		return new ExecutionProgress(ExecutionTimes.STACK_EXECUTION);
	}
}
