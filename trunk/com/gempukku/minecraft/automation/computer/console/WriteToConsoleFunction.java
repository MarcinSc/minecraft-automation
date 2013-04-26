package com.gempukku.minecraft.automation.computer.console;

import com.gempukku.minecraft.automation.computer.ComputerCallback;
import com.gempukku.minecraft.automation.computer.JavaFunctionExecutable;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.world.World;

import java.util.Map;

public class WriteToConsoleFunction extends JavaFunctionExecutable {
	@Override
	protected int getDuration() {
		return 100;
	}

	@Override
	public String[] getParameterNames() {
		return new String[]{"x", "y", "text"};
	}

	@Override
	protected Object executeFunction(int line, World world, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
		final Variable x = parameters.get("x");
		final Variable y = parameters.get("y");
		final Variable text = parameters.get("text");
		if (x.getType() != Variable.Type.NUMBER)
			throw new ExecutionException(line, "Expected NUMBER in write()");
		if (y.getType() != Variable.Type.NUMBER)
			throw new ExecutionException(line, "Expected NUMBER in write()");
		if (text.getType() != Variable.Type.STRING)
			throw new ExecutionException(line, "Expected STRING in write()");

		computer.getConsole().setCharacters(((Number) x.getValue()).intValue(), ((Number) y.getValue()).intValue(), (String) text.getValue());

		return null;
	}
}
