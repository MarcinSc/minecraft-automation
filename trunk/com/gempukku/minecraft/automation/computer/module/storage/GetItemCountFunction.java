package com.gempukku.minecraft.automation.computer.module.storage;

import com.gempukku.minecraft.automation.computer.module.ModuleComputerCallback;
import com.gempukku.minecraft.automation.computer.module.ModuleFunctionExecutable;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Map;

public class GetItemCountFunction implements ModuleFunctionExecutable {
	@Override
	public int getDuration() {
		return 100;
	}

	@Override
	public String[] getParameterNames() {
		return new String[]{"side", "slot"};
	}

	@Override
	public Object executeFunction(int line, World world, ModuleComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
		final String functionName = "getItemCount";
		final Variable sideParam = parameters.get("side");
		final Variable slotParam = parameters.get("slot");

		final IInventory inventory = StorageModuleUtils.getInventoryAtFace(line, computer, world, sideParam, functionName);
		if (inventory == null)
			return null;

		ItemStack stackInSlot = StorageModuleUtils.getStackFromInventory(line, computer, inventory, sideParam, slotParam, functionName);
		return getSizeOfPotentialStack(stackInSlot);
	}

	private int getSizeOfPotentialStack(ItemStack stackInSlot) {
		return stackInSlot != null ? stackInSlot.stackSize : 0;
	}
}
