package com.gempukku.minecraft.automation.client;

import com.gempukku.minecraft.automation.CommonAutomationProxy;
import com.gempukku.minecraft.automation.program.ProgramScheduler;

import java.io.File;

public class ClientAutomationProxy extends CommonAutomationProxy {
    private ClientAutomationRegistry _automationRegistry;

    @Override
    public void initialize(File modConfigDirectory, ProgramScheduler programScheduler) {
        _automationRegistry = new ClientAutomationRegistry();
    }

    @Override
    public ClientAutomationRegistry getRegistry() {
        return _automationRegistry;
    }
}
