package com.gempukku.minecraft.automation.module.mobility;

import com.gempukku.minecraft.BoxSide;
import com.gempukku.minecraft.automation.AutomationUtils;
import com.gempukku.minecraft.automation.ComputerEvent;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.JavaFunctionExecutable;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.util.Map;

public class TurnFunction extends JavaFunctionExecutable {
    private boolean _left;

    public TurnFunction(boolean left) {
        _left = left;
    }

    @Override
    protected int getDuration() {
        return 100;
    }

    @Override
    public String[] getParameterNames() {
        return new String[0];
    }

    @Override
    protected Object executeFunction(ServerComputerData computer, World world, Map<String, Variable> parameters) throws ExecutionException {
        final ComputerTileEntity tileEntity = AutomationUtils.getComputerEntitySafely(world, computer);
        if (tileEntity == null)
            return false;

        int newFacing = _left ? BoxSide.getLeft(computer.getFacing()) : BoxSide.getRight(computer.getFacing());
        world.setBlockMetadataWithNotify(computer.getX(), computer.getY(), computer.getZ(), newFacing, 2);
        MinecraftForge.EVENT_BUS.post(new ComputerEvent.ComputerMovedInWorldEvent(world, tileEntity));

        return true;
    }
}
