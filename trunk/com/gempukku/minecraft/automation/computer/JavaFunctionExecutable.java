package com.gempukku.minecraft.automation.computer;

import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.execution.SimpleExecution;

import java.util.HashMap;
import java.util.Map;

public abstract class JavaFunctionExecutable implements FunctionExecutable {
    @Override
    public Execution createExecution(CallContext callContext) {
        return new SimpleExecution() {
            @Override
            protected ExecutionProgress execute(ExecutionContext context) throws ExecutionException {
                ComputerData computer = ((ComputerExecutionContext) context).getComputerData();

                final String[] parameterNames = getParameterNames();
                Map<String, Variable> parameters = new HashMap<String, Variable>();
                final CallContext callContext = context.peekCallContext();
                for (String parameterName : parameterNames)
                    parameters.put(parameterName, callContext.getVariableValue(parameterName));

                context.setReturnValue(new Variable(executeFunction(computer, parameters)));
                return new ExecutionProgress(getDuration());
            }
        };
    }

    protected abstract Object executeFunction(ComputerData computer, Map<String, Variable> parameters) throws ExecutionException;

    protected abstract int getDuration();
}
