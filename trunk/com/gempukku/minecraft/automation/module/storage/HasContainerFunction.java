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
    @Override
    protected int getDuration() {
        return 100;
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{"side"};
    }

    @Override
    protected Object executeFunction(ServerComputerData computer, World world, Map<String, Variable> parameters) throws ExecutionException {
        int facing = computer.getFacing();
        final Variable sideVar = parameters.get("side");

        if (sideVar.getType() != Variable.Type.STRING)
            throw new ExecutionException("Expected front, top, bottom, left or right in hasContainer function");

        String side = (String) sideVar.getValue();
        if (!side.equals("front") || !side.equals("left") || !side.equals("right") || !side.equals("top") || !side.equals("bottom"))
            throw new ExecutionException("Expected front, top, bottom, left or right in hasContainer function");

        int lookAt = facing;
        if (side.equals("left"))
            lookAt = BoxSide.getLeft(facing);
        else if (side.equals("right"))
            lookAt = BoxSide.getRight(facing);
        else if (side.equals("top"))
            lookAt = BoxSide.TOP;
        else if (side.equals("bottom"))
            lookAt = BoxSide.BOTTOM;

        final TileEntity blockTileEntity = world.getBlockTileEntity(
                computer.getX() + Facing.offsetsXForSide[lookAt],
                computer.getY() + Facing.offsetsYForSide[lookAt],
                computer.getZ() + Facing.offsetsZForSide[lookAt]);

        return blockTileEntity instanceof IInventory;
    }
}
