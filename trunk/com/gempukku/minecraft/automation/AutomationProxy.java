package com.gempukku.minecraft.automation;

import java.io.File;

public interface AutomationProxy {
    public void initialize(File modConfigDirectory);
    public AutomationRegistry getAutomationRegistry();
    public ProgramProcessing getProgramProcessing();
}
