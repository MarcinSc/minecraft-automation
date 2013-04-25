package com.gempukku.minecraft.automation.module.gps;

import com.gempukku.minecraft.automation.computer.JavaFunctionExecutable;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class GetPositionFunction extends JavaFunctionExecutable {
	@Override
	protected int getDuration() {
		return 100;
	}

	@Override
	public String[] getParameterNames() {
		return new String[0];
	}

	@Override
	protected Object executeFunction(int line, World world, ServerComputerData computer, Map<String, Variable> parameters) throws ExecutionException {
		Map<String, Variable> result = new HashMap<String, Variable>();
		result.put("x", new Variable(computer.getX()));
		result.put("y", new Variable(computer.getY()));
		result.put("z", new Variable(computer.getZ()));
		return result;
	}
}
