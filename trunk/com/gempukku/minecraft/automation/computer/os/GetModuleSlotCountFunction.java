package com.gempukku.minecraft.automation.computer.os;

import com.gempukku.minecraft.automation.computer.ComputerData;
import com.gempukku.minecraft.automation.computer.ComputerExecutionContext;
import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.execution.SimpleExecution;

public class GetModuleSlotCountFunction implements FunctionExecutable {
    @Override
    public String[] getParameterNames() {
        return new String[0];
    }

    @Override
    public Execution createExecution(CallContext callContext) {
        return new SimpleExecution() {
            @Override
            protected ExecutionProgress execute(ExecutionContext context) throws ExecutionException {
                ComputerData computer = ((ComputerExecutionContext) context).getComputerData();
                context.setContextValue(new Variable(computer.getModuleSlotCount()));
                return new ExecutionProgress(100);
            }
        };
    }
}
