package com.gempukku.minecraft.automation.module.mobility;

import com.gempukku.minecraft.automation.AutomationUtils;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.FunctionExecutable;
import com.gempukku.minecraft.automation.module.AbstractComputerModule;
import com.gempukku.minecraft.automation.module.ComputerModule;
import net.minecraft.world.World;

public class MobilityModule extends AbstractComputerModule {
    public static final String TYPE = "Mobility";
    private MoveForwardFunction _moveForward = new MoveForwardFunction();
    private TurnFunction _turnLeftFunction = new TurnFunction(true);
    private TurnFunction _turnRightFunction = new TurnFunction(true);

    @Override
    public boolean acceptsNewModule(World world, ServerComputerData computerData, ComputerModule computerModule) {
        return !computerModule.getModuleType().equals(TYPE);
    }

    @Override
    public boolean canBePlacedInComputer(World world, ServerComputerData computerData) {
        final ComputerTileEntity computerTileEntity = AutomationUtils.getComputerEntitySafely(world, computerData);
        if (computerTileEntity == null)
            return false;

        final int slotCount = computerTileEntity.getModuleSlotsCount();
        for (int i = 0; i < slotCount; i++) {
            final ComputerModule module = computerTileEntity.getModule(i);
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
