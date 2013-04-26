package com.gempukku.minecraft.automation.computer.module;

import com.gempukku.minecraft.automation.computer.ComputerCallback;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class AbstractComputerModule implements ComputerModule {
	@Override
	public boolean hasInventoryManipulator() {
		return false;
	}

	@Override
	public int getStorageSlots() {
		return 0;
	}

	@Override
	public boolean acceptsNewModule(World world, ComputerCallback computerCallback, ComputerModule computerModule) {
		return true;
	}

	@Override
	public boolean canBePlacedInComputer(World world, ComputerCallback computerCallback) {
		return true;
	}

	@Override
	public int getStrongRedstoneSignalStrengthOnSide(ServerComputerData computerData, int input, IBlockAccess blockAccess, int side) {
		return input;
	}

	@Override
	public int getWeakRedstoneSignalStrengthOnSide(ServerComputerData computerData, int input, IBlockAccess blockAccess, int side) {
		return input;
	}
}
