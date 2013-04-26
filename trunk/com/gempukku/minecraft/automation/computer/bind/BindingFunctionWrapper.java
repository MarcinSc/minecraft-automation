package com.gempukku.minecraft.automation.computer.bind;

import com.gempukku.minecraft.automation.AutomationUtils;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.ComputerCallback;
import com.gempukku.minecraft.automation.computer.MinecraftComputerExecutionContext;
import com.gempukku.minecraft.automation.computer.module.ComputerModule;
import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.execution.SimpleExecution;

public class BindingFunctionWrapper implements FunctionExecutable {
	private int _slotNo;
	private ComputerModule _module;
	private FunctionExecutable _function;

	public BindingFunctionWrapper(ComputerModule module, int slotNo, FunctionExecutable function) {
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
	public Execution createExecution(int line, ExecutionContext executionContext, final CallContext callContext) {
		final MinecraftComputerExecutionContext minecraftExecutionContext = (MinecraftComputerExecutionContext) executionContext;
		final ComputerCallback computerData = minecraftExecutionContext.getComputerCallback();
		final ComputerTileEntity computerTileEntity = AutomationUtils.getComputerEntitySafely(minecraftExecutionContext.getWorld(), computerData);
		if (computerTileEntity == null)
			return getThrowingExceptionExecution(line);
		final ComputerModule module = computerTileEntity.getModule(_slotNo);
		if (module == _module) {
			return _function.createExecution(line, executionContext, callContext);
		} else {
			return getThrowingExceptionExecution(line);
		}
	}

	private Execution getThrowingExceptionExecution(final int line) {
		return new SimpleExecution() {
			@Override
			protected ExecutionProgress execute(ExecutionContext context) throws ExecutionException {
				throw new ExecutionException(line, "Bound module has been removed or replaced");
			}
		};
	}
}
