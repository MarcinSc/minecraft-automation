package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.ExecutableStatement;
import com.gempukku.minecraft.automation.lang.Execution;
import com.gempukku.minecraft.automation.lang.Operator;
import com.gempukku.minecraft.automation.lang.execution.MathExecution;

public class MathStatement implements ExecutableStatement {
	private ExecutableStatement _left;
	private Operator _operator;
	private ExecutableStatement _right;
	private boolean _assignToLeft;

	public MathStatement(ExecutableStatement left, Operator operator, ExecutableStatement right, boolean assignToLeft) {
		_left = left;
		_operator = operator;
		_right = right;
		_assignToLeft = assignToLeft;
	}

	@Override
	public Execution createExecution() {
		return new MathExecution(_left, _operator, _right, _assignToLeft);
	}

	@Override
	public boolean requiresSemicolon() {
		return false;
	}
}
