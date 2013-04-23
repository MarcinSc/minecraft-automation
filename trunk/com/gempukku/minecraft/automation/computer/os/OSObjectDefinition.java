package com.gempukku.minecraft.automation.computer.os;

import com.gempukku.minecraft.automation.lang.ExecutionContext;
import com.gempukku.minecraft.automation.lang.ObjectDefinition;
import com.gempukku.minecraft.automation.lang.Variable;

public class OSObjectDefinition implements ObjectDefinition {
	private Variable _parseFloat = new Variable(new ParseFloatFunction());

	@Override
	public Variable getMember(ExecutionContext context, String name) {
		if (name.equals("parseFloat"))
			return _parseFloat;

		return new Variable(null);
	}
}
