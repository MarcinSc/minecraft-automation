package com.gempukku.minecraft.automation.computer.module.storage;

import com.gempukku.minecraft.automation.computer.module.ModuleComputerCallback;
import com.gempukku.minecraft.automation.computer.module.ModuleFunctionExecutable;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.world.World;

import java.util.Map;

public class HasContainerFunction implements ModuleFunctionExecutable {
	@Override
	public int getDuration() {
		return 100;
	}

	@Override
	public String[] getParameterNames() {
		return new String[]{"side"};
	}

	@Override
	public Object executeFunction(int line, World world, ModuleComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
		return StorageModuleUtils.getInventoryAtFace(line, computer, world, parameters.get("side"), "hasContainer") != null;
	}
}
