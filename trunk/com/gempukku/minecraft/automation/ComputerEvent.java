package com.gempukku.minecraft.automation;

import net.minecraftforge.event.Event;

public class ComputerEvent extends Event {
    public final int computerId;

    public ComputerEvent(int computerId) {
        this.computerId = computerId;
    }

    public static class ComputerAddedToWorldEvent extends ComputerEvent {
        public ComputerAddedToWorldEvent(int computerId) {
            super(computerId);
        }
    }

    public static class ComputerRemovedFromWorldEvent extends ComputerEvent {
        public ComputerRemovedFromWorldEvent(int computerId) {
            super(computerId);
        }
    }

    public static class ComputerModulesChangedEvent extends ComputerEvent {
        public ComputerModulesChangedEvent(int computerId) {
            super(computerId);
        }
    }
}
