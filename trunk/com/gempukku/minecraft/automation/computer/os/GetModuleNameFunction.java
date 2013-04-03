package com.gempukku.minecraft.automation.computer.os;

import com.gempukku.minecraft.automation.computer.ComputerData;
import com.gempukku.minecraft.automation.computer.ComputerModule;
import com.gempukku.minecraft.automation.computer.JavaFunctionExecutable;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;

import java.util.Map;

public class GetModuleNameFunction extends JavaFunctionExecutable {
    @Override
    public String[] getParameterNames() {
        return new String[]{"slot"};
    }

    @Override
    protected int getDuration() {
        return 100;
    }

    @Override
    protected Object executeFunction(ComputerData computer, Map<String, Variable> parameters) throws ExecutionException {
        final Variable slot = parameters.get("slot");
        if (slot.getType() != Variable.Type.NUMBER)
            throw new ExecutionException("Number expected");

        int slotNo = ((Number) slot.getValue()).intValue();
        final ComputerModule module = computer.getModuleAt(slotNo);
        String moduleType = null;
        if (module != null)
            moduleType = module.getModuleType();
        return moduleType;
    }
}
