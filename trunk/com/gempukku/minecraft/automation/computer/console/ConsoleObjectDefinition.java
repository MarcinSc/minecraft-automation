package com.gempukku.minecraft.automation.computer.console;

import com.gempukku.minecraft.automation.lang.ExecutionContext;
import com.gempukku.minecraft.automation.lang.ObjectDefinition;
import com.gempukku.minecraft.automation.lang.Variable;

public class ConsoleObjectDefinition implements ObjectDefinition {
	private Variable _append = new Variable(new AppendToConsoleFunction());
	private Variable _clear = new Variable(new ClearConsoleFunction());
	private Variable _write = new Variable(new WriteToConsoleFunction());

	@Override
	public Variable getMember(ExecutionContext context, String name) {
		if (name.equals("append"))
			return _append;
		else if (name.equals("clear"))
			return _clear;
		else if (name.equals("write"))
			return _write;
		return new Variable(null);
	}
}
