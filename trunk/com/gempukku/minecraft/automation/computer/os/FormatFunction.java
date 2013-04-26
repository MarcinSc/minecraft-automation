package com.gempukku.minecraft.automation.computer.os;

import com.gempukku.minecraft.automation.computer.ComputerCallback;
import com.gempukku.minecraft.automation.computer.JavaFunctionExecutable;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.world.World;

import java.text.DecimalFormat;
import java.util.Map;

public class FormatFunction extends JavaFunctionExecutable {
	@Override
	protected int getDuration() {
		return 10;
	}

	@Override
	public String[] getParameterNames() {
		return new String[]{"format", "number"};
	}

	@Override
	protected Object executeFunction(int line, World world, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
		final Variable formatVar = parameters.get("format");
		if (formatVar.getType() != Variable.Type.STRING)
			throw new ExecutionException(line, "Expected STRING pattern in format()");
		String format = (String) formatVar.getValue();

		final Variable numberVar = parameters.get("number");
		if (numberVar.getType() != Variable.Type.NUMBER)
			throw new ExecutionException(line, "Expected NUMBER in format()");

		float number = ((Number) numberVar.getValue()).floatValue();

		try {
			return new DecimalFormat(format).format(number);
		} catch (IllegalArgumentException exp) {
			throw new ExecutionException(line, "Invalid format pattern " + format + " in format()");
		}
	}
}
