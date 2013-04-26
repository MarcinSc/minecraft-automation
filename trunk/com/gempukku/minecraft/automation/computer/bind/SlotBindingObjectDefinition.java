package com.gempukku.minecraft.automation.computer.bind;

import com.gempukku.minecraft.automation.AutomationUtils;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.ComputerCallback;
import com.gempukku.minecraft.automation.computer.MinecraftComputerExecutionContext;
import com.gempukku.minecraft.automation.computer.module.ComputerModule;
import com.gempukku.minecraft.automation.computer.module.ModuleFunctionExecutable;
import com.gempukku.minecraft.automation.lang.ExecutionContext;
import com.gempukku.minecraft.automation.lang.ObjectDefinition;
import com.gempukku.minecraft.automation.lang.Variable;

public class SlotBindingObjectDefinition implements ObjectDefinition {
	private int _slotNo;

	public SlotBindingObjectDefinition(int slotNo) {
		_slotNo = slotNo;
	}

	@Override
	public Variable getMember(ExecutionContext context, String name) {
		final MinecraftComputerExecutionContext minecraftExecutionContext = (MinecraftComputerExecutionContext) context;
		final ComputerCallback computerData = minecraftExecutionContext.getComputerCallback();
		final ComputerTileEntity computerTileEntity = AutomationUtils.getComputerEntitySafely(minecraftExecutionContext.getWorld(), computerData);
		if (computerTileEntity == null)
			return new Variable(null);
		final ComputerModule module = computerTileEntity.getModule(_slotNo);
		if (module == null)
			return new Variable(null);

		final ModuleFunctionExecutable moduleFunction = module.getFunctionByName(name);

		if (moduleFunction != null) {
			return new Variable(new BindingFunctionWrapper(module, _slotNo, new ModuleFunctionAdapter(_slotNo, moduleFunction)));
		} else {
			return new Variable(null);
		}
	}
}
