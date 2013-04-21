package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.execution.SimpleExecution;

import java.util.List;

public class DefineFunctionStatement implements ExecutableStatement {
	private String _name;
	private List<String> _parameterNames;
	private List<ExecutableStatement> _statements;

	public DefineFunctionStatement(String name, List<String> parameterNames, List<ExecutableStatement> statements) throws IllegalSyntaxException {
		if (LangDefinition.isReservedWord(name))
			throw new IllegalSyntaxException("Invalid function name");
		_name = name;
		_parameterNames = parameterNames;
		_statements = statements;
	}

	@Override
	public Execution createExecution() {
		return new SimpleExecution() {
			@Override
			protected ExecutionProgress execute(ExecutionContext context) throws ExecutionException {
				final DefaultFunctionExecutable functionExecutable = new DefaultFunctionExecutable(context.peekCallContext(), _parameterNames.toArray(new String[_parameterNames.size()]));
				functionExecutable.setStatement(
								new BlockStatement(_statements, false, true));
				final CallContext callContext = context.peekCallContext();
				final Variable variable = callContext.defineVariable(_name);
				context.setVariableValue(variable, functionExecutable);
				return new ExecutionProgress(ExecutionTimes.SET_VARIABLE);
			}
		};
	}

	@Override
	public boolean requiresSemicolon() {
		return false;
	}
}
