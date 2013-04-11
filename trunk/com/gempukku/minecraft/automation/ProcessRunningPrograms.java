package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.MinecraftUtils;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import net.minecraft.world.World;

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
        World world = (World) tickData[0];
        if (MinecraftUtils.isServer(world))
            Automation.getServerProxy().getProgramProcessing().progressAllPrograms(world);
    }

    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.WORLD);
    }
}
