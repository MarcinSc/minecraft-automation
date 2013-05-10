package com.gempukku.minecraft.automation.computer.module.redstone;

import com.gempukku.minecraft.automation.computer.module.ModuleComputerCallback;
import com.gempukku.minecraft.automation.computer.module.ModuleFunctionExecutable;
import com.gempukku.minecraft.automation.computer.module.storage.StorageModuleUtils;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class EmitStrongSignalFunction implements ModuleFunctionExecutable {
    @Override
    public int getDuration() {
        return 2000;
    }

    @Override
    public String[] getParameterNames() {
        return new String[]{"side", "strength"};
    }

    @Override
    public Object executeFunction(int line, World world, ModuleComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
        final Variable sideParam = parameters.get("side");
        final Variable strengthParam = parameters.get("strength");

        int side = StorageModuleUtils.getComputerFacingSide(line, computer, sideParam, "emitStrongSignal");

        if (strengthParam.getType() != Variable.Type.NUMBER)
            throw new ExecutionException(line, "Expected NUMBER in emitStrongSignal");

        int strength = ((Number) strengthParam.getValue()).intValue();
        if (strength < 0 || strength > 15)
            throw new ExecutionException(line, "Expected strength in range of 0-15");
        Map<String, String> moduleData = new HashMap<String, String>(computer.getModuleData());
        moduleData.put("strong-"+side, String.valueOf(strength));
        computer.setModuleData(moduleData);

        ChunkPosition position = computer.getChunkPosition();
        world.notifyBlocksOfNeighborChange(position.x, position.y, position.z,
                world.getBlockId(position.x, position.y, position.z));

        return null;
    }
}
