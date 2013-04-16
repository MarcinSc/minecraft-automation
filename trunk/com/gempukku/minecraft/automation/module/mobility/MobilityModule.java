package com.gempukku.minecraft.automation.module.mobility;

import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.FunctionExecutable;
import com.gempukku.minecraft.automation.module.AbstractComputerModule;
import com.gempukku.minecraft.automation.module.ComputerModule;

public class MobilityModule extends AbstractComputerModule {
    public static final String TYPE = "Mobility";
    private MoveForwardFunction _forward = new MoveForwardFunction();

    @Override
    public boolean acceptsNewModule(ServerComputerData computerData, ComputerModule computerModule) {
        return !computerModule.getModuleType().equals(TYPE);
    }

    @Override
    public boolean canBePlacedInComputer(ServerComputerData computerData) {
        final int slotCount = computerData.getModuleSlotCount();
        for (int i = 0; i < slotCount; i++) {
            final ComputerModule module = computerData.getModuleAt(i);
            if (module != null && module.getModuleType().equals(TYPE))
                return false;
        }
        return true;
    }

    @Override
    public String getModuleType() {
        return TYPE;
    }

    @Override
    public FunctionExecutable getFunctionByName(String name) {
        if (name.equals("moveForward"))
            return _forward;
        return null;
    }
}
