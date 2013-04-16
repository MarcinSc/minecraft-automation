package com.gempukku.minecraft.automation.computer.bind;

import com.gempukku.minecraft.automation.AutomationUtils;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.MinecraftComputerExecutionContext;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.execution.SimpleExecution;
import com.gempukku.minecraft.automation.module.ComputerModule;

public class BindingFunctionWrapper implements FunctionExecutable {
    private ServerComputerData _computer;
    private int _slotNo;
    private ComputerModule _module;
    private FunctionExecutable _function;

    public BindingFunctionWrapper(ServerComputerData computer, ComputerModule module, int slotNo, FunctionExecutable function) {
        _computer = computer;
        _module = module;
        _slotNo = slotNo;
        _function = function;
    }

    @Override
    public CallContext getCallContext() {
        return new CallContext(null, false, false);
    }

    @Override
    public String[] getParameterNames() {
        return _function.getParameterNames();
    }

    @Override
    public Execution createExecution(ExecutionContext executionContext, final CallContext callContext) {
        final MinecraftComputerExecutionContext minecraftExecutionContext = (MinecraftComputerExecutionContext) executionContext;
        final ServerComputerData computerData = minecraftExecutionContext.getComputerData();
        final ComputerTileEntity computerTileEntity = AutomationUtils.getComputerEntitySafely(minecraftExecutionContext.getWorld(), computerData.getX(), computerData.getY(), computerData.getZ());
        if (computerTileEntity == null)
            return getThrowingExceptionExecution();
        final ComputerModule module = computerTileEntity.getModule(_slotNo);
        if (module == _module) {
            return _function.createExecution(executionContext, callContext);
        } else {
            return getThrowingExceptionExecution();
        }
    }

    private Execution getThrowingExceptionExecution() {
        return new SimpleExecution() {
            @Override
            protected ExecutionProgress execute(ExecutionContext context) throws ExecutionException {
                throw new ExecutionException("Bound module has been removed or replaced");
            }
        };
    }
}
