package com.gempukku.minecraft.automation.computer.module.harvest;

import com.gempukku.minecraft.BoxSide;
import com.gempukku.minecraft.automation.computer.module.ModuleComputerCallback;
import com.gempukku.minecraft.automation.computer.module.ModuleFunctionExecutable;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.block.Block;
import net.minecraft.util.Facing;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.FakePlayer;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.Map;

public class CanHarvestFunction implements ModuleFunctionExecutable {
    @Override
    public int getDuration() {
        return 100;
    }

    @Override
    public int getMinimumExecutionTicks() {
        return 1;
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{"direction"};
    }

    @Override
    public Object executeFunction(int line, World world, ModuleComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
        final Variable directionVar = parameters.get("direction");
        if (directionVar.getType() != Variable.Type.STRING && directionVar.getType() != Variable.Type.NULL)
            throw new ExecutionException(line, "Invalid direction received in harvest()");

        String side = (String) directionVar.getValue();
        if (side != null && (!side.equals("up") && !side.equals("down")))
            throw new ExecutionException(line, "Invalid direction received in harvest()");

        final int facing = computer.getFacing();

        int direction = facing;
        if (side != null) {
            if (side.equals("up"))
                direction = BoxSide.TOP;
            else if (side.equals("down"))
                direction = BoxSide.BOTTOM;
        }

        final ChunkPosition chunkPosition = computer.getChunkPosition();
        final int harvestX = chunkPosition.x + Facing.offsetsXForSide[direction];
        final int harvestY = chunkPosition.y + Facing.offsetsYForSide[direction];
        final int harvestZ = chunkPosition.z + Facing.offsetsZForSide[direction];

        if (!world.getChunkProvider().chunkExists(harvestX >> 4, harvestZ >> 4))
            return false;

        final Block block = Block.blocksList[world.getBlockId(harvestX, harvestY, harvestZ)];
        if (block == null)
            // Can't harvest air
            return false;

        final float blockHardness = block.getBlockHardness(world, harvestX, harvestY, harvestZ);

        return ForgeEventFactory.doPlayerHarvestCheck(new FakePlayer(world, computer.getOwner()), block, blockHardness >= 0);
    }
}
