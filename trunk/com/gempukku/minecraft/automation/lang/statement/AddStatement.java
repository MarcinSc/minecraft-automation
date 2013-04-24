package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.ExecutableStatement;
import com.gempukku.minecraft.automation.lang.Execution;
import com.gempukku.minecraft.automation.lang.execution.AddExecution;

public class AddStatement implements ExecutableStatement {
	private ExecutableStatement _left;
	private ExecutableStatement _right;
	private boolean _assignToLeft;

	public AddStatement(ExecutableStatement left, ExecutableStatement right, boolean assignToLeft) {
		_left = left;
		_right = right;
		_assignToLeft = assignToLeft;
	}

	@Override
	public Execution createExecution() {
		return new AddExecution(_left, _right, _assignToLeft);
	}

	@Override
	public boolean requiresSemicolon() {
		return true;
	}
}
