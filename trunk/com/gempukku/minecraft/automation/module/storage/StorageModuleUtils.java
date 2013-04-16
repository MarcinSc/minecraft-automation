package com.gempukku.minecraft.automation.module.storage;

import com.gempukku.minecraft.BoxSide;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Facing;
import net.minecraft.world.World;

public class StorageModuleUtils {
    public static TileEntity getBlockEntityAtFace(ServerComputerData computer, World world, Variable sideVar, String functionName) throws ExecutionException {
        int lookAt = getComputerFacingSide(computer, sideVar, functionName);

        return world.getBlockTileEntity(
                computer.getX() + Facing.offsetsXForSide[lookAt],
                computer.getY() + Facing.offsetsYForSide[lookAt],
                computer.getZ() + Facing.offsetsZForSide[lookAt]);
    }

    public static int getComputerFacingSide(ServerComputerData computer, Variable sideVar, String functionName) throws ExecutionException {
        int facing = computer.getFacing();

        if (sideVar.getType() != Variable.Type.STRING)
            throw new ExecutionException("Expected front, top, bottom, left or right in " + functionName + " function");

        String side = (String) sideVar.getValue();
        if (!side.equals("front") || !side.equals("left") || !side.equals("right") || !side.equals("top") || !side.equals("bottom"))
            throw new ExecutionException("Expected front, top, bottom, left or right in " + functionName + " function");

        int lookAt = facing;
        if (side.equals("left"))
            lookAt = BoxSide.getLeft(facing);
        else if (side.equals("right"))
            lookAt = BoxSide.getRight(facing);
        else if (side.equals("top"))
            lookAt = BoxSide.TOP;
        else if (side.equals("bottom"))
            lookAt = BoxSide.BOTTOM;
        return lookAt;
    }
}
