package com.gempukku.minecraft.automation.computer.module.positioning;

import com.gempukku.minecraft.automation.computer.module.ModuleComputerCallback;
import com.gempukku.minecraft.automation.computer.module.ModuleFunctionExecutable;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class GetPositionFunction implements ModuleFunctionExecutable {
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
        return new String[0];
    }

    @Override
    public Object executeFunction(int line, World world, ModuleComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
        Map<String, Variable> result = new HashMap<String, Variable>();
        final ChunkPosition chunkPosition = computer.getChunkPosition();
        result.put("x", new Variable(chunkPosition.x));
        result.put("y", new Variable(chunkPosition.y));
        result.put("z", new Variable(chunkPosition.z));
        return result;
    }
}
