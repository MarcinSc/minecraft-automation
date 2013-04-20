package com.gempukku.minecraft.automation;

import net.minecraftforge.event.Event;

public class ComputerEvent extends Event {
	public final int computerId;
	public final int x;
	public final int y;
	public final int z;
	public final int facing;

	public ComputerEvent(int computerId, int x, int y, int z, int facing) {
		this.computerId = computerId;
		this.x = x;
		this.y = y;
		this.z = z;
		this.facing = facing;
	}

	public static class ComputerAddedToWorldEvent extends ComputerEvent {
		public ComputerAddedToWorldEvent(int computerId, int x, int y, int z, int facing) {
			super(computerId, x, y, z, facing);
		}
	}

	public static class ComputerRemovedFromWorldEvent extends ComputerEvent {
		public ComputerRemovedFromWorldEvent(int computerId, int x, int y, int z, int facing) {
			super(computerId, x, y, z, facing);
		}
	}

	public static class ComputerMovedInWorldEvent extends ComputerEvent {
		public ComputerMovedInWorldEvent(int computerId, int x, int y, int z, int facing) {
			super(computerId, x, y, z, facing);
		}
	}

	public static class ComputerModulesChangedEvent extends ComputerEvent {
		public ComputerModulesChangedEvent(int computerId, int x, int y, int z, int facing) {
			super(computerId, x, y, z, facing);
		}
	}
}
