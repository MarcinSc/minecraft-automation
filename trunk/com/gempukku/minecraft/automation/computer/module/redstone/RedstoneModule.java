package com.gempukku.minecraft.automation.computer.module.redstone;

import com.gempukku.minecraft.automation.computer.module.AbstractComputerModule;
import com.gempukku.minecraft.automation.computer.module.ModuleFunctionExecutable;

public class RedstoneModule extends AbstractComputerModule {
	@Override
	public String getModuleType() {
		return "Redstone";
	}

	@Override
	public String getModuleName() {
		return "Redstone module";
	}

	@Override
	public ModuleFunctionExecutable getFunctionByName(String name) {
		return null;
	}
}
