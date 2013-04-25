package com.gempukku.minecraft.automation.computer.computer;

import com.gempukku.minecraft.automation.AutomationUtils;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.JavaFunctionExecutable;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.world.World;

import java.util.Map;

public class GetModuleSlotCountFunction extends JavaFunctionExecutable {
	@Override
	public String[] getParameterNames() {
		return new String[0];
	}

	@Override
	protected int getDuration() {
		return 100;
	}

	@Override
	protected Object executeFunction(int line, World world, ServerComputerData computer, Map<String, Variable> parameters) throws ExecutionException {
		final ComputerTileEntity computerTileEntity = AutomationUtils.getComputerEntitySafely(world, computer);
		if (computerTileEntity == null)
			return 0;

		return computerTileEntity.getModuleSlotsCount();
	}
}
