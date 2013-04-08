package com.gempukku.minecraft.automation;

import java.io.File;

public class AutomationOnClient implements AutomationProxy {
    @Override
    public void initialize(File modConfigDirectory) {
        
    }

    @Override
    public AutomationRegistry getAutomationRegistry() {
        return null;
    }

    @Override
    public ProgramProcessing getProgramProcessing() {
        throw new UnsupportedOperationException("Automation on client has no ProgramProcessing");
    }
}
