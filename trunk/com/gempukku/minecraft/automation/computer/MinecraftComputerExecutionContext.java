package com.gempukku.minecraft.automation.computer;

import com.gempukku.minecraft.automation.lang.ExecutionContext;

public class MinecraftComputerExecutionContext extends ExecutionContext {
	private ServerComputerData _computerData;

	public MinecraftComputerExecutionContext(ServerComputerData computerData) {
		_computerData = computerData;
	}

	public ServerComputerData getComputerData() {
		return _computerData;
	}
}
