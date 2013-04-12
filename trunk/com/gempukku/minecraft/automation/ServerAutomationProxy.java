package com.gempukku.minecraft.automation;

import net.minecraftforge.common.MinecraftForge;

import java.io.File;

public class ServerAutomationProxy implements AutomationProxy {
    private ServerAutomationRegistry _automationRegistry;
    private ComputerProcessing _programProcessing;

    @Override
    public void initialize(File modConfigDirectory) {
        _automationRegistry = new ServerAutomationRegistry(modConfigDirectory);
        _programProcessing = new ComputerProcessing(modConfigDirectory, _automationRegistry);
        MinecraftForge.EVENT_BUS.register(_automationRegistry);
        MinecraftForge.EVENT_BUS.register(_programProcessing);
    }

    public ComputerProcessing getComputerProcessing() {
        return _programProcessing;
    }

    @Override
    public ServerAutomationRegistry getRegistry() {
        return _automationRegistry;
    }
}
