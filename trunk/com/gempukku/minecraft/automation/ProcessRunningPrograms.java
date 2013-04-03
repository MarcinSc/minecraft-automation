package com.gempukku.minecraft.automation;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

import java.util.EnumSet;

public class ProcessRunningPrograms implements ITickHandler {
    @Override
    public String getLabel() {
        return "Automation program processing";
    }

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
        Automation.getProgramProcessing().progressAllPrograms();
    }

    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.SERVER);
    }
}
