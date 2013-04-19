package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import net.minecraftforge.event.Event;

public class ComputerEvent extends Event {
	private final ComputerTileEntity _computerTileEntity;

	protected ComputerEvent(ComputerTileEntity computerTileEntity) {
		_computerTileEntity = computerTileEntity;
	}

	public ComputerTileEntity getComputerTileEntity() {
		return _computerTileEntity;
	}

	public static class ComputerAddedToWorldEvent extends ComputerEvent {
		public ComputerAddedToWorldEvent(ComputerTileEntity computerTileEntity) {
			super(computerTileEntity);
		}
	}

	public static class ComputerRemovedFromWorldEvent extends ComputerEvent {
		public ComputerRemovedFromWorldEvent(ComputerTileEntity computerTileEntity) {
			super(computerTileEntity);
		}
	}

	public static class ComputerMovedInWorldEvent extends ComputerEvent {
		public ComputerMovedInWorldEvent(ComputerTileEntity computerTileEntity) {
			super(computerTileEntity);
		}
	}

	public static class ComputerModulesChangedEvent extends ComputerEvent {
		public ComputerModulesChangedEvent(ComputerTileEntity computerTileEntity) {
			super(computerTileEntity);
		}
	}
}
