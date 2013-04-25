package com.gempukku.minecraft.automation.computer.computer;

import com.gempukku.minecraft.automation.AutomationUtils;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.JavaFunctionExecutable;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.computer.bind.SlotBindingObjectDefinition;
import com.gempukku.minecraft.automation.computer.module.ComputerModule;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.world.World;

import java.util.Map;

public class BindFirstModuleOfTypeFunction extends JavaFunctionExecutable {
	@Override
	protected int getDuration() {
		return 100;
	}

	@Override
	public String[] getParameterNames() {
		return new String[]{"type"};
	}

	@Override
	protected Object executeFunction(int line, World world, ServerComputerData computer, Map<String, Variable> parameters) throws ExecutionException {
		final Variable type = parameters.get("type");
		if (type.getType() != Variable.Type.STRING)
			throw new ExecutionException(line, "Expected type of module in bindModuleOfType()");

		String moduleType = (String) type.getValue();

		final ComputerTileEntity computerTileEntity = AutomationUtils.getComputerEntitySafely(world, computer);
		if (computerTileEntity == null)
			return null;

		final int moduleSlotsCount = computerTileEntity.getModuleSlotsCount();
		for (int i = 0; i < moduleSlotsCount; i++) {
			final ComputerModule module = computerTileEntity.getModule(i);
			if (module != null && module.getModuleType().equals(moduleType))
				return new SlotBindingObjectDefinition(computer, i);
		}
		return null;
	}
}
