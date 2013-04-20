package com.gempukku.minecraft.automation.computer.os;

import com.gempukku.minecraft.automation.AutomationUtils;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.JavaFunctionExecutable;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import com.gempukku.minecraft.automation.module.ComputerModule;

import java.util.Map;

public class GetModuleTypeFunction extends JavaFunctionExecutable {
	@Override
	public String[] getParameterNames() {
		return new String[]{"slot"};
	}

	@Override
	protected int getDuration() {
		return 100;
	}

	@Override
	protected Object executeFunction(ServerComputerData computer, Map<String, Variable> parameters) throws ExecutionException {
		final Variable slot = parameters.get("slot");
		if (slot.getType() != Variable.Type.NUMBER)
			throw new ExecutionException("Number expected");

		int slotNo = ((Number) slot.getValue()).intValue();
		final ComputerTileEntity computerTileEntity = AutomationUtils.getComputerEntitySafely(computer);
		if (computerTileEntity == null)
			return null;

		final ComputerModule module = computerTileEntity.getModule(slotNo);
		String moduleType = null;
		if (module != null)
			moduleType = module.getModuleType();
		return moduleType;
	}
}
