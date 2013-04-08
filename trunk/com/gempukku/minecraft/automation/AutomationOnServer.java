package com.gempukku.minecraft.automation;

import net.minecraftforge.common.MinecraftForge;

import java.io.File;

public class AutomationOnServer implements AutomationProxy {
    private AutomationRegistry _registry;
    private ProgramProcessing _programProcessing;

    @Override
    public void initialize(File modConfigDirectory) {
        _registry = new ServerAutomationRegistry();
        _programProcessing = new ProgramProcessing(modConfigDirectory, _registry);
        // Register Registry for events
        MinecraftForge.EVENT_BUS.register(_registry);
        MinecraftForge.EVENT_BUS.register(_programProcessing);
    }

    @Override
    public ProgramProcessing getProgramProcessing() {
        return _programProcessing;
    }

    @Override
    public AutomationRegistry getAutomationRegistry() {
        return _registry;
    }
}
