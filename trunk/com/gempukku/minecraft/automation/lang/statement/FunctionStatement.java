package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.execution.SimpleExecution;

import java.util.List;

public class FunctionStatement implements ExecutableStatement {
	private List<String> _parameterNames;
	private List<ExecutableStatement> _statements;

	public FunctionStatement(List<String> parameterNames, List<ExecutableStatement> statements) {
		_parameterNames = parameterNames;
		_statements = statements;
	}

	@Override
	public Execution createExecution() {
		return new SimpleExecution() {
			@Override
			protected ExecutionProgress execute(ExecutionContext context) throws ExecutionException {
				final DefaultFunctionExecutable functionExecutable = new DefaultFunctionExecutable(context.peekCallContext(),
								_parameterNames.toArray(new String[_parameterNames.size()]));
				functionExecutable.setStatement(
								new BlockStatement(_statements, false, true));
				context.setContextValue(new Variable(functionExecutable));
				return new ExecutionProgress(ExecutionTimes.SET_VARIABLE);
			}
		};
	}

	@Override
	public boolean requiresSemicolon() {
		return false;
	}
}
