package com.gempukku.minecraft.automation.computer;

import com.gempukku.minecraft.automation.lang.ExecutionContext;
import net.minecraft.world.World;

public class MinecraftComputerExecutionContext extends ExecutionContext {
    private ComputerData _computerData;
    private World _world;

    public MinecraftComputerExecutionContext(ComputerData computerData) {
        _computerData = computerData;
    }

    public void setWorld(World world) {
        _world = world;
    }

    public World getWorld() {
        return _world;
    }

    public ComputerData getComputerData() {
        return _computerData;
    }
}
