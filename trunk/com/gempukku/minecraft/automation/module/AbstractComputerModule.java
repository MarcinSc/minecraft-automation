package com.gempukku.minecraft.automation.module;

import com.gempukku.minecraft.automation.computer.ComputerData;
import net.minecraft.world.IBlockAccess;

public abstract class AbstractComputerModule implements ComputerModule {
    @Override
    public int getStrongRedstoneSignalStrengthOnSide(ComputerData computerData, int input, IBlockAccess blockAccess, int side) {
        return input;
    }

    @Override
    public int getWeakRedstoneSignalStrengthOnSide(ComputerData computerData, int input, IBlockAccess blockAccess, int side) {
        return input;
    }
}
