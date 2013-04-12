package com.gempukku.minecraft.automation.module;

import com.gempukku.minecraft.automation.computer.ComputerData;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class AbstractComputerModule implements ComputerModule {
    @Override
    public boolean acceptsNewModule(ComputerData computerData, ComputerModule computerModule) {
        return true;
    }

    @Override
    public boolean canBePlacedInComputer(ComputerData computerData) {
        return true;
    }

    @Override
    public int getStrongRedstoneSignalStrengthOnSide(ComputerData computerData, int input, IBlockAccess blockAccess, int side) {
        return input;
    }

    @Override
    public int getWeakRedstoneSignalStrengthOnSide(ComputerData computerData, int input, IBlockAccess blockAccess, int side) {
        return input;
    }

    @Override
    public void onTick(World world, ComputerData computerData) {
    }
}
