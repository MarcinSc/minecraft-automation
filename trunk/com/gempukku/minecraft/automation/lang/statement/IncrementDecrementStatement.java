package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.ExecutableStatement;
import com.gempukku.minecraft.automation.lang.Execution;
import com.gempukku.minecraft.automation.lang.execution.IncrementDecrementExecution;

public class IncrementDecrementStatement implements ExecutableStatement {
	private ExecutableStatement _expression;
	private boolean _increment;
	private boolean _pre;

	public IncrementDecrementStatement(ExecutableStatement expression, boolean increment, boolean pre) {
		_expression = expression;
		_increment = increment;
		_pre = pre;
	}

	@Override
	public boolean requiresSemicolon() {
		return false;
	}

	@Override
	public Execution createExecution() {
		return new IncrementDecrementExecution(_expression, _increment, _pre);
	}
}
