package com.gempukku.minecraft.automation.computer.module.positioning;

import com.gempukku.minecraft.automation.computer.module.AbstractComputerModule;
import com.gempukku.minecraft.automation.lang.FunctionExecutable;

public class PositioningModule extends AbstractComputerModule {
	private FunctionExecutable _getPositionFunction = new GetPositionFunction();
	private FunctionExecutable _getFacingFunction = new GetFacingFunction();

	@Override
	public String getModuleType() {
		return "Positioning";
	}

	@Override
	public String getModuleName() {
		return "Positioning module";
	}

	@Override
	public FunctionExecutable getFunctionByName(String name) {
		if (name.equals("getPosition"))
			return _getPositionFunction;
		else if (name.equals("getFacing"))
			return _getFacingFunction;
		return null;
	}
}
