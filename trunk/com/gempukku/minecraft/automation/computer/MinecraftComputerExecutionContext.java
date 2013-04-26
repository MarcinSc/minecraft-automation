package com.gempukku.minecraft.automation.computer;

import com.gempukku.minecraft.automation.lang.ExecutionContext;
import net.minecraft.world.World;

public class MinecraftComputerExecutionContext extends ExecutionContext {
	private World _world;
	private ComputerCallback _computerCallback;

	public MinecraftComputerExecutionContext(ComputerCallback computerCallback) {
		_computerCallback = computerCallback;
	}

	public World getWorld() {
		return _world;
	}

	public void setWorld(World world) {
		_world = world;
	}

	public ComputerCallback getComputerCallback() {
		return _computerCallback;
	}
}
