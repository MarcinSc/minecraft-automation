package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.DefiningExecutableStatement;
import com.gempukku.minecraft.automation.lang.ExecutableStatement;
import com.gempukku.minecraft.automation.lang.Execution;
import com.gempukku.minecraft.automation.lang.IllegalSyntaxException;
import com.gempukku.minecraft.automation.lang.execution.DefineAndAssignExecution;

public class DefineAndAssignStatement implements DefiningExecutableStatement {
	private String _name;
	private ExecutableStatement _value;

	public DefineAndAssignStatement(String name, ExecutableStatement value) throws IllegalSyntaxException {
		_name = name;
		_value = value;
	}

	@Override
	public String getDefinedVariableName() {
		return _name;
	}

	@Override
	public Execution createExecution() {
		return new DefineAndAssignExecution(_name, _value);
	}

	@Override
	public boolean requiresSemicolon() {
		return true;
	}
}
