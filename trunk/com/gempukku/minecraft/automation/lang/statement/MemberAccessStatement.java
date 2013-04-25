package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.ExecutableStatement;
import com.gempukku.minecraft.automation.lang.Execution;
import com.gempukku.minecraft.automation.lang.IllegalSyntaxException;
import com.gempukku.minecraft.automation.lang.execution.MemberAccessExecution;

public class MemberAccessStatement implements ExecutableStatement {
	private int _line;
	private ExecutableStatement _object;
	private String _propertyName;

	public MemberAccessStatement(int line, ExecutableStatement object, String propertyName) throws IllegalSyntaxException {
		_line = line;
		_object = object;
		_propertyName = propertyName;
	}

	@Override
	public Execution createExecution() {
		return new MemberAccessExecution(_line, _object, _propertyName);
	}

	@Override
	public boolean requiresSemicolon() {
		return true;
	}
}
