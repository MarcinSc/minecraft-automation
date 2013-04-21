package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.execution.MultiStatementExecution;

import java.util.List;

public class BlockStatement implements ExecutableStatement {
	private List<ExecutableStatement> _statements;
	private boolean _consumesBreak;
	private boolean _consumesReturn;

	public BlockStatement(List<ExecutableStatement> statements, boolean consumesBreak, boolean consumesReturn) {
		_statements = statements;
		_consumesBreak = consumesBreak;
		_consumesReturn = consumesReturn;
	}

	public Execution createExecution() {
		return new Execution() {
			private boolean _stacked;

			@Override
			public boolean hasNextExecution(ExecutionContext executionContext) {
				return !_stacked;
			}

			@Override
			public ExecutionProgress executeNextStatement(ExecutionContext executionContext) throws ExecutionException {
				CallContext blockContext = new CallContext(executionContext.peekCallContext(), _consumesBreak, _consumesReturn);
				executionContext.stackExecutionGroup(blockContext, new MultiStatementExecution(_statements));
				_stacked = true;
				return new ExecutionProgress(ExecutionTimes.STACK_GROUP_EXECUTION);
			}
		};
	}

	@Override
	public boolean requiresSemicolon() {
		return true;
	}
}
