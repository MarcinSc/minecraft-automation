package com.gempukku.minecraft.automation.module.mobility;

import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.FunctionExecutable;
import com.gempukku.minecraft.automation.module.AbstractComputerModule;
import com.gempukku.minecraft.automation.module.ComputerModule;

public class MobilityModule extends AbstractComputerModule {
    public static final String TYPE = "Mobility";
    private MoveForwardFunction _moveForward = new MoveForwardFunction();
    private TurnFunction _turnLeftFunction = new TurnFunction(true);
    private TurnFunction _turnRightFunction = new TurnFunction(true);

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
            return _moveForward;
        else if (name.equals("turnLeft"))
            return _turnLeftFunction;
        else if (name.equals("turnRight"))
            return _turnRightFunction;
        return null;
    }
}
