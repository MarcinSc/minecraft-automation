package com.gempukku.minecraft.automation.computer.module.mobility;

import com.gempukku.minecraft.BoxSide;
import com.gempukku.minecraft.MinecraftUtils;
import com.gempukku.minecraft.automation.AutomationUtils;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.module.ModuleComputerCallback;
import com.gempukku.minecraft.automation.computer.module.ModuleFunctionExecutable;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.world.World;

import java.util.Map;

public class TurnFunction implements ModuleFunctionExecutable {
    private boolean _left;

    public TurnFunction(boolean left) {
        _left = left;
    }

    @Override
    public int getDuration() {
        return 100;
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{""};
    }

    @Override
    public Object executeFunction(int line, World world, ModuleComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
        final ComputerTileEntity tileEntity = AutomationUtils.getComputerEntitySafely(world, computer);
        if (tileEntity == null)
            return false;

        int newFacing = _left ? BoxSide.getLeft(computer.getFacing()) : BoxSide.getRight(computer.getFacing());
        tileEntity.setFacing(newFacing);
        MinecraftUtils.sendTileEntityUpdateToPlayers(world, tileEntity);

        return true;
    }
}
