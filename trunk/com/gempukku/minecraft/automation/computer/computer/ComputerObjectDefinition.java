package com.gempukku.minecraft.automation.computer.computer;

import com.gempukku.minecraft.automation.lang.ExecutionContext;
import com.gempukku.minecraft.automation.lang.ObjectDefinition;
import com.gempukku.minecraft.automation.lang.Variable;

public class ComputerObjectDefinition implements ObjectDefinition {
	private Variable _bindModule = new Variable(new BindModuleFunction());
	private Variable _getModuleSlotCount = new Variable(new GetModuleSlotCountFunction());
	private Variable _getModuleType = new Variable(new GetModuleTypeFunction());

	@Override
	public Variable getMember(ExecutionContext context, String name) {
		if (name.equals("bindModule"))
			return _bindModule;
		else if (name.equals("getModuleSlotCount"))
			return _getModuleSlotCount;
		else if (name.equals("getModuleType"))
			return _getModuleType;
		return new Variable(null);
	}
}
