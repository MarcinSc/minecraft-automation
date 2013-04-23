package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.ExecutableStatement;
import com.gempukku.minecraft.automation.lang.Execution;

public class NamedStatement implements ExecutableStatement {
	private String _name;

	public NamedStatement(String name) {
		_name = name;
	}

	public String getName() {
		return _name;
	}

	@Override
	public Execution createExecution() {
		return null;
	}

	@Override
	public boolean requiresSemicolon() {
		return false;
	}
}
