package com.gempukku.minecraft.automation.computer.os;

import com.gempukku.minecraft.automation.computer.JavaFunctionExecutable;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;

import java.util.Map;

public class ClearConsoleFunction extends JavaFunctionExecutable {
	@Override
	protected int getDuration() {
		return 100;
	}

	@Override
	public String[] getParameterNames() {
		return new String[0];
	}

	@Override
	protected Object executeFunction(ServerComputerData computer, Map<String, Variable> parameters) throws ExecutionException {
		computer.getConsole().clearConsole();
		return null;
	}
}
