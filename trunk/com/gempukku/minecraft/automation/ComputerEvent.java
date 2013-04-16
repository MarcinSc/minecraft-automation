package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.Event;

public class ComputerEvent extends Event {
    private World _world;
    private final ComputerTileEntity _computerTileEntity;

    protected ComputerEvent(World world, ComputerTileEntity computerTileEntity) {
        _world = world;
        _computerTileEntity = computerTileEntity;
    }

    public World getWorld() {
        return _world;
    }

    public ComputerTileEntity getComputerTileEntity() {
        return _computerTileEntity;
    }

    public static class ComputerAddedToWorldEvent extends ComputerEvent {
        public ComputerAddedToWorldEvent(World world, ComputerTileEntity computerTileEntity) {
            super(world, computerTileEntity);
        }
    }

    public static class ComputerRemovedFromWorldEvent extends ComputerEvent {
        public ComputerRemovedFromWorldEvent(World world, ComputerTileEntity computerTileEntity) {
            super(world, computerTileEntity);
        }
    }

    public static class ComputerMovedInWorldEvent extends ComputerEvent {
        public ComputerMovedInWorldEvent(World world, ComputerTileEntity computerTileEntity) {
            super(world, computerTileEntity);
        }
    }

    public static class ComputerModulesChangedEvent extends ComputerEvent {
        public ComputerModulesChangedEvent(World world, ComputerTileEntity computerTileEntity) {
            super(world, computerTileEntity);
        }
    }
}
