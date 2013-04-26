package com.gempukku.minecraft.automation.computer.computer;

import com.gempukku.minecraft.automation.AutomationUtils;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.ComputerCallback;
import com.gempukku.minecraft.automation.computer.JavaFunctionExecutable;
import com.gempukku.minecraft.automation.computer.module.ComputerModule;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.world.World;

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
	protected Object executeFunction(int line, World world, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
		final Variable slot = parameters.get("slot");
		if (slot.getType() != Variable.Type.NUMBER)
			throw new ExecutionException(line, "Invalid slot number in getModuleType()");

		int slotNo = ((Number) slot.getValue()).intValue();
		final ComputerTileEntity computerTileEntity = AutomationUtils.getComputerEntitySafely(world, computer);
		if (computerTileEntity == null)
			return null;

		final ComputerModule module = computerTileEntity.getModule(slotNo);
		String moduleType = null;
		if (module != null)
			moduleType = module.getModuleType();
		return moduleType;
	}
}
