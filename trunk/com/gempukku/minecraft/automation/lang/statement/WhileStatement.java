package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.execution.SimpleExecution;
import com.gempukku.minecraft.automation.lang.execution.WhileExecution;

public class WhileStatement implements ExecutableStatement {
	private ExecutableStatement _condition;
	private ExecutableStatement _statement;

	public WhileStatement(ExecutableStatement condition, ExecutableStatement statement) {
		_condition = condition;
		_statement = statement;
	}

	@Override
	public Execution createExecution() {
		return new SimpleExecution() {
			@Override
			protected ExecutionProgress execute(ExecutionContext context) throws ExecutionException {
				CallContext whileContext = new CallContext(context.peekCallContext(), true, false);
				context.stackExecutionGroup(whileContext,
								new WhileExecution(_condition, _statement));
				return new ExecutionProgress(ExecutionTimes.STACK_GROUP_EXECUTION);
			}
		};
	}

	@Override
	public boolean requiresSemicolon() {
		return false;
	}
}
