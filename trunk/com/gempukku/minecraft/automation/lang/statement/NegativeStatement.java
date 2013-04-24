package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.ExecutableStatement;
import com.gempukku.minecraft.automation.lang.Execution;
import com.gempukku.minecraft.automation.lang.execution.NegativeExecution;

public class NegativeStatement implements ExecutableStatement {
	private ExecutableStatement _expression;

	public NegativeStatement(ExecutableStatement expression) {
		_expression = expression;
	}

	@Override
	public Execution createExecution() {
		return new NegativeExecution(_expression);
	}

	@Override
	public boolean requiresSemicolon() {
		return false;
	}
}
