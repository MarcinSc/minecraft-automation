package com.gempukku.minecraft.automation;

import java.io.File;

public class AutomationOnClient implements AutomationProxy {
    private AutomationRegistry _registry;

    @Override
    public void initialize(File modConfigDirectory) {
        _registry = new ClientAutomationRegistry();
    }

    @Override
    public AutomationRegistry getAutomationRegistry() {
        return _registry;
    }

    @Override
    public ProgramProcessing getProgramProcessing() {
        throw new UnsupportedOperationException("Automation on client has no ProgramProcessing");
    }
}
