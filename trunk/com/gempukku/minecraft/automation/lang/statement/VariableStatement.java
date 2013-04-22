package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.execution.SimpleExecution;

public class VariableStatement implements ExecutableStatement {
	private String _name;

	public VariableStatement(String name) throws IllegalSyntaxException {
		_name = name;
	}

	public Execution createExecution() {
		return new SimpleExecution() {
			@Override
			protected ExecutionProgress execute(ExecutionContext context) throws ExecutionException {
				context.setContextValue(context.peekCallContext().getVariableValue(_name));
				return new ExecutionProgress(ExecutionTimes.SET_CONTEXT_VALUE);
			}
		};
	}

	public String getName() {
		return _name;
	}

	@Override
	public boolean requiresSemicolon() {
		return true;
	}
}
