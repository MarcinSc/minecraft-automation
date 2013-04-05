package com.gempukku.minecraft.automation.module.gps;

import com.gempukku.minecraft.automation.computer.ComputerModule;
import com.gempukku.minecraft.automation.lang.FunctionExecutable;

public class GPSModule implements ComputerModule {
    private FunctionExecutable _getPositionFunction = new GetPositionFunction();
    private FunctionExecutable _getFacingFunction = new GetFacingFunction();

    @Override
    public String getModuleType() {
        return "GPS";
    }

    @Override
    public FunctionExecutable getFunctionByName(String name) {
        if (name.equals("getPosition"))
            return _getPositionFunction;
        else if (name.equals("getFacing"))
            return _getFacingFunction;
        return null;
    }
}
