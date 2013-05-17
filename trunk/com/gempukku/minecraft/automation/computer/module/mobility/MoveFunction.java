package com.gempukku.minecraft.automation.computer.module.mobility;

import com.gempukku.minecraft.BoxSide;
import com.gempukku.minecraft.MinecraftUtils;
import com.gempukku.minecraft.automation.AutomationUtils;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.module.ModuleComputerCallback;
import com.gempukku.minecraft.automation.computer.module.ModuleFunctionExecutable;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.block.material.Material;
import net.minecraft.util.Facing;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

import java.util.Map;

public class MoveFunction implements ModuleFunctionExecutable {
    @Override
    public int getDuration() {
        return 10000;
    }

    @Override
    public int getMinimumExecutionTicks() {
        return 20;
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{"direction"};
    }

    @Override
    public Object executeFunction(int line, World world, ModuleComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {

        final int facing = computer.getFacing();
        Variable sideVar = parameters.get("direction");

        if (sideVar.getType() != Variable.Type.STRING)
            throw new ExecutionException(line, "Invalid direction received in move()");

        String side = (String) sideVar.getValue();
        if (!side.equals("forward") && !side.equals("up") && !side.equals("down"))
            throw new ExecutionException(line, "Invalid direction received in move()");

        int direction = facing;
        if (side.equals("up"))
            direction = BoxSide.BOTTOM;
        else if (side.equals("down"))
            direction = BoxSide.TOP;

        final ChunkPosition chunkPosition = computer.getChunkPosition();
        final int newX = chunkPosition.x + Facing.offsetsXForSide[direction];
        final int newY = chunkPosition.y + Facing.offsetsYForSide[direction];
        final int newZ = chunkPosition.z + Facing.offsetsZForSide[direction];

        if (!world.getChunkProvider().chunkExists(newX >> 4, newZ >> 4))
            return false;

        final Material blockMaterial = world.getBlockMaterial(newX, newY, newZ);
        if (!blockMaterial.isReplaceable())
            return false;

        final ComputerTileEntity tileEntity = AutomationUtils.getComputerEntitySafely(world, computer);
        if (tileEntity == null)
            return false;

        final int blockId = world.getBlockId(chunkPosition.x, chunkPosition.y, chunkPosition.z);

        tileEntity.setMoving(true);

        world.setBlockToAir(chunkPosition.x, chunkPosition.y, chunkPosition.z);
        world.removeBlockTileEntity(chunkPosition.x, chunkPosition.y, chunkPosition.z);

        world.setBlock(newX, newY, newZ, blockId, computer.getId(), 2);
        MinecraftUtils.setTileEntity(world, newX, newY, newZ, tileEntity);
        tileEntity.setMoving(false);

        return true;
    }
}
