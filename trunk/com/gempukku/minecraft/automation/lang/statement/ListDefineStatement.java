package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.ExecutableStatement;
import com.gempukku.minecraft.automation.lang.Execution;
import com.gempukku.minecraft.automation.lang.execution.ListDefineExecution;

import java.util.List;

public class ListDefineStatement implements ExecutableStatement {
	private List<ExecutableStatement> _values;

	public ListDefineStatement(List<ExecutableStatement> values) {
		_values = values;
	}

	@Override
	public boolean requiresSemicolon() {
		return false;
	}

	@Override
	public Execution createExecution() {
		return new ListDefineExecution(_values);
	}
}
