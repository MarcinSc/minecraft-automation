package com.gempukku.minecraft.automation.module;

import com.gempukku.minecraft.automation.computer.ServerComputerData;
import net.minecraft.world.IBlockAccess;

public abstract class AbstractComputerModule implements ComputerModule {
    @Override
    public boolean acceptsNewModule(ServerComputerData computerData, ComputerModule computerModule) {
        return true;
    }

    @Override
    public boolean canBePlacedInComputer(ServerComputerData computerData) {
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
