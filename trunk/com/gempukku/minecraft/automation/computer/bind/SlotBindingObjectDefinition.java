package com.gempukku.minecraft.automation.computer.bind;

import com.gempukku.minecraft.automation.AutomationUtils;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.MinecraftComputerExecutionContext;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.ExecutionContext;
import com.gempukku.minecraft.automation.lang.FunctionExecutable;
import com.gempukku.minecraft.automation.lang.ObjectDefinition;
import com.gempukku.minecraft.automation.lang.Variable;
import com.gempukku.minecraft.automation.module.ComputerModule;

public class SlotBindingObjectDefinition implements ObjectDefinition {
	private ServerComputerData _computerData;
	private int _slotNo;

	public SlotBindingObjectDefinition(ServerComputerData computerData, int slotNo) {
		_computerData = computerData;
		_slotNo = slotNo;
	}

	@Override
	public Variable getMember(ExecutionContext context, String name) {
		final MinecraftComputerExecutionContext minecraftExecutionContext = (MinecraftComputerExecutionContext) context;
		final ServerComputerData computerData = minecraftExecutionContext.getComputerData();
		final ComputerTileEntity computerTileEntity = AutomationUtils.getComputerEntitySafely(minecraftExecutionContext.getWorld(), computerData);
		if (computerTileEntity == null)
			return new Variable(null);
		final ComputerModule module = computerTileEntity.getModule(_slotNo);
		if (module == null)
			return new Variable(null);

		final FunctionExecutable function = module.getFunctionByName(name);
		return new Variable(new BindingFunctionWrapper(_computerData, module, _slotNo, function));
	}
}
