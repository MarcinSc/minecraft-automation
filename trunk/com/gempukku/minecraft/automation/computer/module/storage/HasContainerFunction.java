package com.gempukku.minecraft.automation.computer.module.storage;

import com.gempukku.minecraft.automation.computer.JavaFunctionExecutable;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.world.World;

import java.util.Map;

public class HasContainerFunction extends JavaFunctionExecutable {
	@Override
	protected int getDuration() {
		return 100;
	}

	@Override
	public String[] getParameterNames() {
		return new String[]{"side"};
	}

	@Override
	protected Object executeFunction(int line, World world, ServerComputerData computer, Map<String, Variable> parameters) throws ExecutionException {
		return StorageModuleUtils.getInventoryAtFace(line, computer, world, parameters.get("side"), "hasContainer") != null;
	}
}
