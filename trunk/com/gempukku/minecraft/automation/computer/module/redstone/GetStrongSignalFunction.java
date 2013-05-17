package com.gempukku.minecraft.automation.computer.module.redstone;

import com.gempukku.minecraft.BoxSide;
import com.gempukku.minecraft.automation.computer.module.ModuleComputerCallback;
import com.gempukku.minecraft.automation.computer.module.ModuleFunctionExecutable;
import com.gempukku.minecraft.automation.computer.module.storage.StorageModuleUtils;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.util.Facing;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

import java.util.Map;

public class GetStrongSignalFunction implements ModuleFunctionExecutable {
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
        return new String[]{"side"};
    }

    @Override
    public Object executeFunction(int line, World world, ModuleComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
        final Variable sideParam = parameters.get("side");
        int side = StorageModuleUtils.getComputerFacingSide(line, computer, sideParam, "getStrongSignal");

        ChunkPosition position = computer.getChunkPosition();
        return world.isBlockProvidingPowerTo(
                position.x + Facing.offsetsXForSide[side],
                position.y + Facing.offsetsYForSide[side],
                position.z + Facing.offsetsZForSide[side], BoxSide.getOpposite(side));
    }
}
