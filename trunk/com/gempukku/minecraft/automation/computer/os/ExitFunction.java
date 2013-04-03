package com.gempukku.minecraft.automation.computer.os;

import com.gempukku.minecraft.automation.computer.ComputerData;
import com.gempukku.minecraft.automation.computer.JavaFunctionExecutable;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;

import java.util.Map;

public class ExitFunction extends JavaFunctionExecutable {
    @Override
    public String[] getParameterNames() {
        return new String[0];
    }

    @Override
    protected int getDuration() {
        return 100;
    }

    @Override
    protected Object executeFunction(ComputerData computer, Map<String, Variable> parameters) throws ExecutionException {
        return 0;
    }
}
