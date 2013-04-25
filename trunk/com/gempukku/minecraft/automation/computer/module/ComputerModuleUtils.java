package com.gempukku.minecraft.automation.computer.module;

import com.gempukku.minecraft.automation.AutomationUtils;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import net.minecraft.world.World;

public class ComputerModuleUtils {
	public static boolean canManipulateInventories(World world, ServerComputerData computerData) {
		final ComputerTileEntity computerTileEntity = AutomationUtils.getComputerEntitySafely(world, computerData);
		if (computerTileEntity == null)
			return false;

		final int moduleCount = computerTileEntity.getModuleSlotsCount();
		for (int i = 0; i < moduleCount; i++) {
			final ComputerModule module = computerTileEntity.getModule(i);
			if (module != null && module.hasInventoryManipulator())
				return true;
		}
		return false;
	}

	public static boolean hasModuleOfType(World world, ServerComputerData computerData, String moduleType) {
		final ComputerTileEntity computerTileEntity = AutomationUtils.getComputerEntitySafely(world, computerData);
		if (computerTileEntity == null)
			return false;

		final int slotCount = computerTileEntity.getModuleSlotsCount();
		for (int i = 0; i < slotCount; i++) {
			final ComputerModule module = computerTileEntity.getModule(i);
			if (module != null && module.getModuleType().equals(moduleType))
				return true;
		}
		return false;
	}
}
