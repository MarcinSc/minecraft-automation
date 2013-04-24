package com.gempukku.minecraft.automation.lang.execution;

import com.gempukku.minecraft.automation.lang.*;

import java.util.ArrayList;
import java.util.List;

public class ListDefineExecution implements Execution {
	private List<ExecutableStatement> _executableStatements;
	private int _nextStackIndex;
	private int _nextRetrieveIndex;

	private boolean _assignedResult;

	private List<Variable> _result = new ArrayList<Variable>();

	public ListDefineExecution(List<ExecutableStatement> executableStatements) {
		_executableStatements = executableStatements;
	}

	@Override
	public boolean hasNextExecution(ExecutionContext executionContext) {
		return !_assignedResult;
	}

	@Override
	public ExecutionProgress executeNextStatement(ExecutionContext executionContext) throws ExecutionException {
		if (_nextRetrieveIndex < _nextStackIndex) {
			_result.add(new Variable(executionContext.getContextValue().getValue()));
			_nextRetrieveIndex++;
			return new ExecutionProgress(ExecutionTimes.GET_CONTEXT_VALUE);
		}
		if (_nextStackIndex < _executableStatements.size()) {
			executionContext.stackExecution(_executableStatements.get(_nextStackIndex).createExecution());
			_nextStackIndex++;
			return new ExecutionProgress(ExecutionTimes.STACK_EXECUTION);
		}
		if (!_assignedResult) {
			executionContext.setContextValue(new Variable(_result));
			_assignedResult = true;
			return new ExecutionProgress(ExecutionTimes.SET_CONTEXT_VALUE);
		}
		return null;
	}
}
