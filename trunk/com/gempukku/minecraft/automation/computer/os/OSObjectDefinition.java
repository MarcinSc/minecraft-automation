package com.gempukku.minecraft.automation.computer.os;

import com.gempukku.minecraft.automation.lang.ExecutionContext;
import com.gempukku.minecraft.automation.lang.ObjectDefinition;
import com.gempukku.minecraft.automation.lang.Variable;

public class OSObjectDefinition implements ObjectDefinition {
	private Variable _bindModule = new Variable(new BindModuleFunction());
	private Variable _getModuleSlotCount = new Variable(new GetModuleSlotCountFunction());
	private Variable _getModuleType = new Variable(new GetModuleTypeFunction());
	private Variable _appendToConsole = new Variable(new AppendToConsoleFunction());
	private Variable _clearConsole = new Variable(new ClearConsoleFunction());
	private Variable _writeToConsole = new Variable(new WriteToConsoleFunction());

	@Override
	public Variable getMember(ExecutionContext context, String name) {
		if (name.equals("bindModule"))
			return _bindModule;
		else if (name.equals("getModuleSlotCount"))
			return _getModuleSlotCount;
		else if (name.equals("getModuleType"))
			return _getModuleType;
		else if (name.equals("appendToConsole"))
			return _appendToConsole;
		else if (name.equals("clearConsole"))
			return _clearConsole;
		else if (name.equals("writeToConsole"))
			return _writeToConsole;

		return new Variable(null);
	}
}
