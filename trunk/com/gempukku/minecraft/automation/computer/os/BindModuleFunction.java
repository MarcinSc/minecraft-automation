package com.gempukku.minecraft.automation.computer.os;

import com.gempukku.minecraft.automation.computer.bind.SlotBindingObjectDefinition;
import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.execution.ReturnExecution;
import com.gempukku.minecraft.automation.lang.execution.SimpleExecution;

public class BindModuleFunction implements FunctionExecutable {
    @Override
    public Execution createExecution(CallContext callContext) {
        return new ReturnExecution(
                new ExecutableStatement() {
                    @Override
                    public Execution createExecution() {
                        return new SimpleExecution() {
                            @Override
                            protected ExecutionProgress execute(ExecutionContext context) throws ExecutionException {
                                final Variable slot = context.peekCallContext().getVariableValue("slot");
                                if (slot.getType() != Variable.Type.NUMBER)
                                    throw new ExecutionException("Number expected");
                                int slotNo = ((Number) slot.getValue()).intValue();
                                context.setContextValue(new Variable(new SlotBindingObjectDefinition(slotNo)));
                                return new ExecutionProgress(100);
                            }
                        };
                    }

                    @Override
                    public boolean requiresSemicolon() {
                        return false;
                    }
                });
    }

    @Override
    public String[] getParameterNames() {
        return new String[] {"slot"};
    }
}
