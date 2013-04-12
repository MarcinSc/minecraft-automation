package com.gempukku.minecraft.automation.computer;

import com.gempukku.minecraft.automation.lang.ExecutionContext;
import net.minecraft.world.World;

public class MinecraftComputerExecutionContext extends ExecutionContext {
    private ServerComputerData _computerData;
    private World _world;

    public MinecraftComputerExecutionContext(ServerComputerData computerData) {
        _computerData = computerData;
    }

    public void setWorld(World world) {
        _world = world;
    }

    public World getWorld() {
        return _world;
    }

    public ServerComputerData getComputerData() {
        return _computerData;
    }
}
