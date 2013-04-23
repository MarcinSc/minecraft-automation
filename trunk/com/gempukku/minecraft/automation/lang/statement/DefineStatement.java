package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.execution.SimpleExecution;

public class DefineStatement implements DefiningExecutableStatement {
	private String _name;

	public DefineStatement(String name) throws IllegalSyntaxException {
		_name = name;
	}

	@Override
	public String getDefinedVariableName() {
		return _name;
	}

	@Override
	public Execution createExecution() {
		return new SimpleExecution() {
			@Override
			protected ExecutionProgress execute(ExecutionContext context) throws ExecutionException {
				context.peekCallContext().defineVariable(_name);
				return new ExecutionProgress(ExecutionTimes.DEFINE_VARIABLE);
			}
		};
	}

	@Override
	public boolean requiresSemicolon() {
		return true;
	}
}
