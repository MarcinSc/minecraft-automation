package com.gempukku.minecraft.automation;

import java.io.File;

public class AutomationOnServer implements AutomationProxy {
    private AutomationRegistry _registry;
    private ProgramProcessing _programProcessing;

    @Override
    public void initialize(File modConfigDirectory) {
        _registry = new ServerAutomationRegistry();
        _programProcessing = new ProgramProcessing(modConfigDirectory, _registry);
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
