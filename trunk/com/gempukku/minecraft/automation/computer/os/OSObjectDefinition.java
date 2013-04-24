package com.gempukku.minecraft.automation.computer.os;

import com.gempukku.minecraft.automation.lang.ExecutionContext;
import com.gempukku.minecraft.automation.lang.ObjectDefinition;
import com.gempukku.minecraft.automation.lang.Variable;

public class OSObjectDefinition implements ObjectDefinition {
	private Variable _parseFloat = new Variable(new ParseFloatFunction());
	private Variable _parseInt = new Variable(new ParseIntFunction());
	private Variable _typeOf = new Variable(new TypeOfFunction());
	private Variable _waitFor = new Variable(new WaitForFunction());

	private Variable _createSleepMs = new Variable(new CreateSleepMsFunction());
	private Variable _createSleepTick = new Variable(new CreateSleepTickFunction());
	private Variable _any = new Variable(new AnyFunction());
	private Variable _all = new Variable(new AllFunction());

	@Override
	public Variable getMember(ExecutionContext context, String name) {
		if (name.equals("parseFloat"))
			return _parseFloat;
		else if (name.equals("parseInt"))
			return _parseInt;
		else if (name.equals("typeOf"))
			return _typeOf;
		else if (name.equals("waitFor"))
			return _waitFor;
		else if (name.equals("createSleepMs"))
			return _createSleepMs;
		else if (name.equals("createSleepTick"))
			return _createSleepTick;
		else if (name.equals("any"))
			return _any;
		else if (name.equals("all"))
			return _all;

		return new Variable(null);
	}
}
