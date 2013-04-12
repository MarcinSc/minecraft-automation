package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.automation.computer.ServerComputerData;
import net.minecraft.world.World;
import net.minecraftforge.event.Event;

public class ComputerEvent extends Event {
    private World _world;
    private final ServerComputerData _computerData;

    protected ComputerEvent(World world, ServerComputerData computerData) {
        _world = world;
        _computerData = computerData;
    }

    public World getWorld() {
        return _world;
    }

    public ServerComputerData getComputerData() {
        return _computerData;
    }

    public static class ComputerAddedToWorldEvent extends ComputerEvent {
        public ComputerAddedToWorldEvent(World world, ServerComputerData computerData) {
            super(world, computerData);
        }
    }

    public static class ComputerRemovedFromWorldEvent extends ComputerEvent {
        public ComputerRemovedFromWorldEvent(World world, ServerComputerData computerData) {
            super(world, computerData);
        }
    }
}
