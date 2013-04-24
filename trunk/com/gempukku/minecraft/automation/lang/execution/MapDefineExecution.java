package com.gempukku.minecraft.automation.lang.execution;

import com.gempukku.minecraft.automation.lang.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapDefineExecution implements Execution {
	private Iterator<Map.Entry<String, ExecutableStatement>> _propertiesIterator;
	private String _lastKey;
	private boolean _hasToAssign;

	private boolean _finished;
	private Map<String, Variable> _result = new HashMap<String, Variable>();

	public MapDefineExecution(Map<String, ExecutableStatement> properties) {
		_propertiesIterator = properties.entrySet().iterator();
	}

	@Override
	public boolean hasNextExecution(ExecutionContext executionContext) {
		if (_finished)
			return false;

		return true;
	}

	@Override
	public ExecutionProgress executeNextStatement(ExecutionContext executionContext) throws ExecutionException {
		if (_hasToAssign) {
			_result.put(_lastKey, new Variable(executionContext.getContextValue().getValue()));
			_hasToAssign = false;
			return new ExecutionProgress(ExecutionTimes.GET_CONTEXT_VALUE);
		}
		if (_propertiesIterator.hasNext()) {
			final Map.Entry<String, ExecutableStatement> property = _propertiesIterator.next();
			_lastKey = property.getKey();
			_hasToAssign = true;
			executionContext.stackExecution(property.getValue().createExecution());
			return new ExecutionProgress(ExecutionTimes.STACK_EXECUTION);
		}
		if (!_finished) {
			_finished = true;
			executionContext.setContextValue(new Variable(_result));
			return new ExecutionProgress(ExecutionTimes.SET_CONTEXT_VALUE);
		}
		return null;
	}
}
