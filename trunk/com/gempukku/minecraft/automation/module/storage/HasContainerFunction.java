package com.gempukku.minecraft.automation.module.storage;

import com.gempukku.minecraft.BoxSide;
import com.gempukku.minecraft.automation.computer.JavaFunctionExecutable;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Facing;
import net.minecraft.world.World;

import java.util.Map;

public class HasContainerFunction extends JavaFunctionExecutable {
    private ComputerSide _computerSide;

    public HasContainerFunction(ComputerSide computerSide) {
        _computerSide = computerSide;
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
        int facing = computer.getFacing();
        int lookAt = facing;
        if (_computerSide == ComputerSide.LEFT)
            lookAt = BoxSide.getLeft(facing);
        else if (_computerSide == ComputerSide.RIGHT)
            lookAt = BoxSide.getRight(facing);

        final TileEntity blockTileEntity = world.getBlockTileEntity(
                computer.getX() + Facing.offsetsXForSide[lookAt],
                computer.getY() + Facing.offsetsYForSide[lookAt],
                computer.getZ() + Facing.offsetsZForSide[lookAt]);
        return blockTileEntity instanceof IInventory;
    }
}
