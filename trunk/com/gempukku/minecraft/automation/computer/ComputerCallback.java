package com.gempukku.minecraft.automation.computer;

import net.minecraft.world.ChunkPosition;

public interface ComputerCallback {
	public int getId();

	public int getFacing();

	public String getOwner();

	public ChunkPosition getChunkPosition();

	public ComputerConsole getConsole();
}
