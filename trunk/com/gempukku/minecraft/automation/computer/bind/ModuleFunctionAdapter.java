package com.gempukku.minecraft.automation.computer.bind;

import com.gempukku.minecraft.automation.computer.ComputerCallback;
import com.gempukku.minecraft.automation.computer.DelayedExecution;
import com.gempukku.minecraft.automation.computer.MinecraftComputerExecutionContext;
import com.gempukku.minecraft.automation.computer.module.ModuleFunctionExecutable;
import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.execution.SimpleExecution;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class ModuleFunctionAdapter implements FunctionExecutable {
    private int _slotNo;
    private ModuleFunctionExecutable _moduleFunction;

    public ModuleFunctionAdapter(int slotNo, ModuleFunctionExecutable moduleFunction) {
        _slotNo = slotNo;
        _moduleFunction = moduleFunction;
    }

    @Override
    public CallContext getCallContext() {
        return new CallContext(null, false, false);
    }

    @Override
    public String[] getParameterNames() {
        return _moduleFunction.getParameterNames();
    }

    @Override
    public Execution createExecution(final int line, ExecutionContext executionContext, CallContext callContext) {
        return new DelayedExecution(_moduleFunction.getDuration(), _moduleFunction.getMinimumExecutionTicks(),
                new SimpleExecution() {
                    @Override
                    protected ExecutionProgress execute(ExecutionContext context) throws ExecutionException {
                        final MinecraftComputerExecutionContext minecraftExecutionContext = (MinecraftComputerExecutionContext) context;
                        ComputerCallback computer = minecraftExecutionContext.getComputerCallback();

                        final String[] parameterNames = getParameterNames();
                        Map<String, Variable> parameters = new HashMap<String, Variable>();
                        final CallContext callContext = context.peekCallContext();
                        for (String parameterName : parameterNames)
                            parameters.put(parameterName, callContext.getVariableValue(parameterName));

                        final World world = minecraftExecutionContext.getWorld();
                        context.setReturnValue(new Variable(_moduleFunction.executeFunction(line, world, new ModuleComputerCallbackImpl(world, _slotNo, computer), parameters)));
                        return new ExecutionProgress(ExecutionTimes.SET_RETURN_VALUE);
                    }
                });
    }
}
