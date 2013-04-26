package com.gempukku.minecraft.automation.computer.module.positioning;

import com.gempukku.minecraft.automation.computer.module.AbstractComputerModule;
import com.gempukku.minecraft.automation.computer.module.ModuleFunctionExecutable;

public class PositioningModule extends AbstractComputerModule {
	private ModuleFunctionExecutable _getPositionFunction = new GetPositionFunction();
	private ModuleFunctionExecutable _getFacingFunction = new GetFacingFunction();

	@Override
	public String getModuleType() {
		return "Positioning";
	}

	@Override
	public String getModuleName() {
		return "Positioning module";
	}

	@Override
	public ModuleFunctionExecutable getFunctionByName(String name) {
		if (name.equals("getPosition"))
			return _getPositionFunction;
		else if (name.equals("getFacing"))
			return _getFacingFunction;
		return null;
	}
}
