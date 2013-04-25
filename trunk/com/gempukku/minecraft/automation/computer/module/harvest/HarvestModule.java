package com.gempukku.minecraft.automation.computer.module.harvest;

import com.gempukku.minecraft.automation.computer.module.AbstractComputerModule;
import com.gempukku.minecraft.automation.lang.FunctionExecutable;

public class HarvestModule extends AbstractComputerModule {
	public static final String TYPE = "Harvest";
	private FunctionExecutable _canHarvest = new CanHarvestFunction();
	private FunctionExecutable _harvest = new HarvestFunction();

	@Override
	public String getModuleType() {
		return TYPE;
	}

	@Override
	public String getModuleName() {
		return "Harvest module";
	}

	@Override
	public FunctionExecutable getFunctionByName(String name) {
		if (name.equals("canHarvest"))
			return _canHarvest;
		else if (name.equals("harvest"))
			return _harvest;
		return null;
	}
}
