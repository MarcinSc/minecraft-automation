package com.gempukku.minecraft.automation.lang.execution;

import com.gempukku.minecraft.automation.lang.*;

public class MemberAccessExecution implements Execution {
	private ExecutableStatement _object;
	private String _propertyName;

	private boolean _objectStacked;
	private boolean _objectResolved;

	private boolean _memberAccessStored;

	private Variable _objectValue;

	public MemberAccessExecution(ExecutableStatement object, String propertyName) {
		_object = object;
		_propertyName = propertyName;
	}

	@Override
	public boolean hasNextExecution(ExecutionContext executionContext) {
		if (!_objectStacked)
			return true;
		if (!_objectResolved)
			return true;
		if (!_memberAccessStored)
			return true;
		return false;
	}

	@Override
	public ExecutionProgress executeNextStatement(ExecutionContext executionContext) throws ExecutionException {
		if (!_objectStacked) {
			executionContext.stackExecution(_object.createExecution());
			_objectStacked = true;
			return new ExecutionProgress(ExecutionTimes.STACK_EXECUTION);
		}
		if (!_objectResolved) {
			_objectValue = executionContext.getContextValue();
			_objectResolved = true;
			return new ExecutionProgress(ExecutionTimes.GET_CONTEXT_VALUE);
		}
		if (!_memberAccessStored) {
			executionContext.setContextValue(executionContext.resolveMember(_objectValue, _propertyName));
			_memberAccessStored = true;
			return new ExecutionProgress(ExecutionTimes.SET_CONTEXT_VALUE + ExecutionTimes.RESOLVE_MEMBER);
		}
		return null;
	}
}
