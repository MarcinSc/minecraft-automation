package com.gempukku.minecraft.automation.computer.bind;

import com.gempukku.minecraft.automation.computer.ComputerData;
import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.execution.SimpleExecution;
import com.gempukku.minecraft.automation.module.ComputerModule;

public class BindingFunctionWrapper implements FunctionExecutable {
    private ComputerData _computer;
    private int _slotNo;
    private ComputerModule _module;
    private FunctionExecutable _function;

    public BindingFunctionWrapper(ComputerData computer, ComputerModule module, int slotNo, FunctionExecutable function) {
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
    public Execution createExecution(CallContext callContext) {
        final ComputerModule module = _computer.getModuleAt(_slotNo);
        if (module == _module) {
            return _function.createExecution(callContext);
        } else {
            return new SimpleExecution() {
                @Override
                protected ExecutionProgress execute(ExecutionContext context) throws ExecutionException {
                    throw new ExecutionException("Bound module has been removed or replaced");
                }
            };
        }
    }
}
