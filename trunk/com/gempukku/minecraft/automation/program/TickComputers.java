package com.gempukku.minecraft.automation.program;

import com.gempukku.minecraft.automation.Automation;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

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
		Automation.getServerProxy().getComputerProcessing().tickComputers();
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.SERVER);
	}
}
