package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.ExecutableStatement;
import com.gempukku.minecraft.automation.lang.Execution;
import com.gempukku.minecraft.automation.lang.execution.AddExecution;

public class AddStatement implements ExecutableStatement {
	private int _line;
	private ExecutableStatement _left;
	private ExecutableStatement _right;
	private boolean _assignToLeft;

	public AddStatement(int line, ExecutableStatement left, ExecutableStatement right, boolean assignToLeft) {
		_line = line;
		_left = left;
		_right = right;
		_assignToLeft = assignToLeft;
	}

	@Override
	public Execution createExecution() {
		return new AddExecution(_line, _left, _right, _assignToLeft);
	}

	@Override
	public boolean requiresSemicolon() {
		return true;
	}
}
