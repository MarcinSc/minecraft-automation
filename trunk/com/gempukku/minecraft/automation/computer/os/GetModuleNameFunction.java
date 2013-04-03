package com.gempukku.minecraft.automation.computer.os;

import com.gempukku.minecraft.automation.computer.ComputerData;
import com.gempukku.minecraft.automation.computer.ComputerExecutionContext;
import com.gempukku.minecraft.automation.computer.ComputerModule;
import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.execution.SimpleExecution;

public class GetModuleNameFunction implements FunctionExecutable {
    @Override
    public String[] getParameterNames() {
        return new String[]{"slot"};
    }

    @Override
    public Execution createExecution(CallContext callContext) {
        return new SimpleExecution() {
            @Override
            protected ExecutionProgress execute(ExecutionContext context) throws ExecutionException {
                ComputerData computer = ((ComputerExecutionContext) context).getComputerData();

                final Variable slot = context.peekCallContext().getVariableValue("slot");
                if (slot.getType() != Variable.Type.NUMBER)
                    throw new ExecutionException("Number expected");

                int slotNo = ((Number) slot.getValue()).intValue();
                final ComputerModule module = computer.getModuleAt(slotNo);
                String moduleType = null;
                if (module != null)
                    moduleType = module.getModuleType();
                context.setContextValue(new Variable(moduleType));
                return new ExecutionProgress(100);
            }
        };
    }
}
