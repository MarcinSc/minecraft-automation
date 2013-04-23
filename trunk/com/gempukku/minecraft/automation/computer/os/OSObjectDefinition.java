package com.gempukku.minecraft.automation.computer.os;

import com.gempukku.minecraft.automation.lang.ExecutionContext;
import com.gempukku.minecraft.automation.lang.ObjectDefinition;
import com.gempukku.minecraft.automation.lang.Variable;

public class OSObjectDefinition implements ObjectDefinition {
	private Variable _parseFloat = new Variable(new ParseFloatFunction());
	private Variable _parseInt = new Variable(new ParseIntFunction());
	private Variable _typeOf = new Variable(new TypeOfFunction());

	@Override
	public Variable getMember(ExecutionContext context, String name) {
		if (name.equals("parseFloat"))
			return _parseFloat;
		else if (name.equals("parseInt"))
			return _parseInt;

		return new Variable(null);
	}
}
