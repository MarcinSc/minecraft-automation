package com.gempukku.minecraft.automation.computer;

import com.gempukku.minecraft.automation.lang.FunctionExecutable;

public interface ComputerModule {
    public String getModuleType();
    public FunctionExecutable getFunctionByName(String name);
}
