package com.gempukku.minecraft.automation.computer.console;

import com.gempukku.minecraft.automation.computer.ComputerCallback;
import com.gempukku.minecraft.automation.computer.JavaFunctionExecutable;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.world.World;

import java.util.Map;

public class AppendToConsoleFunction extends JavaFunctionExecutable {
	@Override
	protected int getDuration() {
		return 100;
	}

	@Override
	public String[] getParameterNames() {
		return new String[]{"text"};
	}

	@Override
	protected Object executeFunction(int line, World world, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
		final Variable text = parameters.get("text");
		if (text.getType() != Variable.Type.STRING)
			throw new ExecutionException(line, "Expected STRING in append()");
		computer.getConsole().appendString((String) text.getValue());
		return null;
	}
}
