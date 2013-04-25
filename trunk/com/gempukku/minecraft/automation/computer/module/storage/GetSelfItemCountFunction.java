package com.gempukku.minecraft.automation.computer.module.storage;

import com.gempukku.minecraft.automation.AutomationUtils;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.JavaFunctionExecutable;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Map;

public class GetSelfItemCountFunction extends JavaFunctionExecutable {
	@Override
	protected int getDuration() {
		return 100;
	}

	@Override
	public String[] getParameterNames() {
		return new String[]{"slot"};
	}

	@Override
	protected Object executeFunction(int line, World world, ServerComputerData computer, Map<String, Variable> parameters) throws ExecutionException {
		String functionName = "getSelfItemCount";

		final Variable slotParam = parameters.get("slot");
		if (slotParam.getType() != Variable.Type.NUMBER)
			throw new ExecutionException(line, "Expected number in slot parameter in " + functionName + " function");

		final ComputerTileEntity computerTileEntity = AutomationUtils.getComputerEntitySafely(world, computer);
		if (computerTileEntity == null)
			return null;

		int slot = ((Number) slotParam.getValue()).intValue();

		int inventorySize = computerTileEntity.getSizeInventory();

		if (inventorySize <= slot || slot < 0)
			throw new ExecutionException(line, "Slot number out of accepted range in " + functionName + " function");

		ItemStack stackInSlot = computerTileEntity.getStackInSlot(slot);
		return getSizeOfPotentialStack(stackInSlot);
	}

	private int getSizeOfPotentialStack(ItemStack stackInSlot) {
		return stackInSlot != null ? stackInSlot.stackSize : 0;
	}
}
