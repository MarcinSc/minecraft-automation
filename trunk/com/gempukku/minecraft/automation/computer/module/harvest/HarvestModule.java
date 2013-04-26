package com.gempukku.minecraft.automation.computer.module.harvest;

import com.gempukku.minecraft.automation.computer.module.AbstractComputerModule;
import com.gempukku.minecraft.automation.computer.module.ModuleFunctionExecutable;

public class HarvestModule extends AbstractComputerModule {
	public static final String TYPE = "Harvest";
	private ModuleFunctionExecutable _canHarvest = new CanHarvestFunction();
	private ModuleFunctionExecutable _harvest = new HarvestFunction();

	@Override
	public String getModuleType() {
		return TYPE;
	}

	@Override
	public String getModuleName() {
		return "Harvest module";
	}

	@Override
	public ModuleFunctionExecutable getFunctionByName(String name) {
		if (name.equals("canHarvest"))
			return _canHarvest;
		else if (name.equals("harvest"))
			return _harvest;
		return null;
	}
}
