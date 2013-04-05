package com.gempukku.minecraft.automation.computer.os;

import com.gempukku.minecraft.automation.computer.ComputerData;
import com.gempukku.minecraft.automation.computer.JavaFunctionExecutable;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.world.World;

import java.util.Map;

public class GetModuleSlotCountFunction extends JavaFunctionExecutable {
    @Override
    public String[] getParameterNames() {
        return new String[0];
    }

    @Override
    protected int getDuration() {
        return 100;
    }

    @Override
    protected Object executeFunction(ComputerData computer, World world, Map<String, Variable> parameters) throws ExecutionException {
        return computer.getModuleSlotCount();
    }
}
