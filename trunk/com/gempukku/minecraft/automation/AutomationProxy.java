package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.automation.program.ProgramScheduler;

import java.io.File;

public interface AutomationProxy {
    public void initialize(File modConfigDirectory, ProgramScheduler programScheduler);

    public AutomationRegistry getRegistry();
}
