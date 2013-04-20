package com.gempukku.minecraft.automation.computer.os;

import com.gempukku.minecraft.automation.computer.JavaFunctionExecutable;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;

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
	protected Object executeFunction(ServerComputerData computer, Map<String, Variable> parameters) throws ExecutionException {
		final Variable x = parameters.get("x");
		final Variable y = parameters.get("y");
		final Variable text = parameters.get("text");
		if (x.getType() != Variable.Type.NUMBER)
			throw new ExecutionException("Expected NUMBER, got " + x.getType());
		if (y.getType() != Variable.Type.NUMBER)
			throw new ExecutionException("Expected NUMBER, got " + y.getType());
		if (text.getType() != Variable.Type.STRING)
			throw new ExecutionException("Expected STRING, got " + text.getType());

		computer.getConsole().setCharacters(((Number) x.getValue()).intValue(), ((Number) y.getValue()).intValue(), (String) text.getValue());

		return null;
	}
}
