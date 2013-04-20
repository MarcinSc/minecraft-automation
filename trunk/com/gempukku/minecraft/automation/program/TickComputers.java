package com.gempukku.minecraft.automation.program;

import com.gempukku.minecraft.MinecraftUtils;
import com.gempukku.minecraft.automation.Automation;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import net.minecraft.world.World;

import java.util.EnumSet;

public class TickComputers implements ITickHandler {
	@Override
	public String getLabel() {
		return "Automation program processing";
	}

	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		final World world = (World) tickData[0];
		if (MinecraftUtils.isServer(world))
			Automation.getServerProxy().getComputerProcessing().tickComputersInWorld(world);
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.WORLD);
	}
}
