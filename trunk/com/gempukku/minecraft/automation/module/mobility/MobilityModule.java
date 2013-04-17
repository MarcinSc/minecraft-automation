package com.gempukku.minecraft.automation.module.mobility;

import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.FunctionExecutable;
import com.gempukku.minecraft.automation.module.AbstractComputerModule;
import com.gempukku.minecraft.automation.module.ComputerModule;
import com.gempukku.minecraft.automation.module.ComputerModuleUtils;
import net.minecraft.world.World;

public class MobilityModule extends AbstractComputerModule {
    public static final String TYPE = "Mobility";
    private MoveFunction _move = new MoveFunction();
    private CanMoveFunction _canMove = new CanMoveFunction();
    private TurnFunction _turnLeftFunction = new TurnFunction(true);
    private TurnFunction _turnRightFunction = new TurnFunction(true);

    @Override
    public boolean acceptsNewModule(World world, ServerComputerData computerData, ComputerModule computerModule) {
        return !computerModule.getModuleType().equals(TYPE);
    }

    @Override
    public boolean canBePlacedInComputer(World world, ServerComputerData computerData) {
        return !ComputerModuleUtils.hasModuleOfType(world, computerData, TYPE);
    }

    @Override
    public String getModuleType() {
        return TYPE;
    }

    @Override
    public String getModuleName() {
        return "Mobility module";
    }

    @Override
    public FunctionExecutable getFunctionByName(String name) {
        if (name.equals("move"))
            return _move;
        else if (name.equals("turnLeft"))
            return _turnLeftFunction;
        else if (name.equals("turnRight"))
            return _turnRightFunction;
        else if (name.equals("canMove"))
            return _canMove;
        return null;
    }
}
