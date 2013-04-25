package com.gempukku.minecraft.automation.computer.os;

import com.gempukku.minecraft.automation.computer.JavaFunctionExecutable;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.world.World;

import java.util.Map;

public class ParseFloatFunction extends JavaFunctionExecutable {
	@Override
	protected int getDuration() {
		return 10;
	}

	@Override
	public String[] getParameterNames() {
		return new String[]{"text"};
	}

	@Override
	protected Object executeFunction(int line, World world, ServerComputerData computer, Map<String, Variable> parameters) throws ExecutionException {
		final Variable text = parameters.get("text");
		if (text.getType() != Variable.Type.STRING)
			throw new ExecutionException(line, "Expected STRING in parseFloat()");
		try {
			return Float.parseFloat((String) text.getValue());
		} catch (NumberFormatException exp) {
			throw new ExecutionException(line, "Number format exception: " + text.getValue() + " in parseFloat()");
		}
	}
}
