package com.gempukku.minecraft.automation.module.mobility;

import com.gempukku.minecraft.automation.lang.FunctionExecutable;
import com.gempukku.minecraft.automation.module.AbstractComputerModule;

public class MobilityModule extends AbstractComputerModule {
    private MoveForwardFunction _forward = new MoveForwardFunction();

    @Override
    public String getModuleType() {
        return "Mobility";
    }

    @Override
    public FunctionExecutable getFunctionByName(String name) {
        if (name.equals("forward"))
            return _forward;
        return null;
    }
}
