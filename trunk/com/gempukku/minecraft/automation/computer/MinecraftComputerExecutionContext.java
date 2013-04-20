package com.gempukku.minecraft.automation.computer;

import com.gempukku.minecraft.automation.lang.ExecutionContext;
import net.minecraft.world.World;

public class MinecraftComputerExecutionContext extends ExecutionContext {
	private World _world;
	private ServerComputerData _computerData;

	public MinecraftComputerExecutionContext(ServerComputerData computerData) {
		_computerData = computerData;
	}

	public World getWorld() {
		return _world;
	}

	public void setWorld(World world) {
		_world = world;
	}

	public ServerComputerData getComputerData() {
		return _computerData;
	}
}
