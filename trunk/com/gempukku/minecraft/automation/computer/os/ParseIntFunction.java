package com.gempukku.minecraft.automation.computer.os;

import com.gempukku.minecraft.automation.computer.JavaFunctionExecutable;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.world.World;

import java.util.Map;

public class ParseIntFunction extends JavaFunctionExecutable {
	@Override
	protected int getDuration() {
		return 10;
	}

	@Override
	public String[] getParameterNames() {
		return new String[]{"text"};
	}

	@Override
	protected Object executeFunction(World world, ServerComputerData computer, Map<String, Variable> parameters) throws ExecutionException {
		final Variable text = parameters.get("text");
		if (text.getType() != Variable.Type.STRING)
			throw new ExecutionException("STRING expected");
		try {
			return Integer.parseInt((String) text.getValue());
		} catch (NumberFormatException exp) {
			throw new ExecutionException("Number format exception: " + text.getValue());
		}
	}
}
