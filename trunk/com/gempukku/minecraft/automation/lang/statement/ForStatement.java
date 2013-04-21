package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.execution.ForExecution;
import com.gempukku.minecraft.automation.lang.execution.SimpleExecution;

public class ForStatement implements ExecutableStatement {
	private ExecutableStatement _initializationStatement;
	private ExecutableStatement _terminationCondition;
	private ExecutableStatement _executedAfterEachLoop;
	private ExecutableStatement _statementInLoop;

	public ForStatement(ExecutableStatement initializationStatement, ExecutableStatement terminationCondition, ExecutableStatement executedAfterEachLoop, ExecutableStatement statementInLoop) {
		_initializationStatement = initializationStatement;
		_terminationCondition = terminationCondition;
		_executedAfterEachLoop = executedAfterEachLoop;
		_statementInLoop = statementInLoop;
	}

	@Override
	public Execution createExecution() {
		return new SimpleExecution() {
			@Override
			protected ExecutionProgress execute(ExecutionContext context) throws ExecutionException {
				CallContext forContext = new CallContext(context.peekCallContext(), true, false);
				context.stackExecutionGroup(forContext,
								new ForExecution(_initializationStatement, _terminationCondition, _executedAfterEachLoop, _statementInLoop));
				return new ExecutionProgress(ExecutionTimes.STACK_GROUP_EXECUTION);
			}
		};
	}

	@Override
	public boolean requiresSemicolon() {
		return false;
	}
}
