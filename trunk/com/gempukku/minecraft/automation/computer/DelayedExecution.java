package com.gempukku.minecraft.automation.computer;

import com.gempukku.minecraft.automation.lang.Execution;
import com.gempukku.minecraft.automation.lang.ExecutionContext;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.ExecutionProgress;

public class DelayedExecution implements Execution {
	private boolean _delayed;
	private int _delay;
	private Execution _execution;

	public DelayedExecution(int delay, Execution execution) {
		_delay = delay;
		_execution = execution;
	}

	@Override
	public boolean hasNextExecution(ExecutionContext executionContext) {
		return !_delayed || _execution.hasNextExecution(executionContext);
	}

	@Override
	public ExecutionProgress executeNextStatement(ExecutionContext executionContext) throws ExecutionException {
		if (!_delayed) {
			_delayed = true;
			return new ExecutionProgress(_delay);
		}
		return _execution.executeNextStatement(executionContext);
	}
}
