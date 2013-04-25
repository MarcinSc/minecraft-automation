package com.gempukku.minecraft.automation.module.storage;

import com.gempukku.minecraft.BoxSide;
import com.gempukku.minecraft.automation.AutomationUtils;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.JavaFunctionExecutable;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Map;

public class TransferToSelfFunction extends JavaFunctionExecutable {
	@Override
	protected int getDuration() {
		return 100;
	}

	@Override
	public String[] getParameterNames() {
		return new String[]{"side", "slot", "count"};
	}

	@Override
	protected Object executeFunction(int line, World world, ServerComputerData computer, Map<String, Variable> parameters) throws ExecutionException {
		final Variable sideParam = parameters.get("side");
		final Variable slotParam = parameters.get("slot");
		final Variable countParam = parameters.get("count");

		int count;
		if (countParam.getType() == Variable.Type.NULL)
			count = Integer.MAX_VALUE;
		else if (countParam.getType() == Variable.Type.NUMBER)
			count = ((Number) countParam.getValue()).intValue();
		else
			throw new ExecutionException(line, "Expected NUMBER or NULL in transferToSelf()");

		final String functionName = "transferToSelf";

		final ComputerTileEntity computerTileEntity = AutomationUtils.getComputerEntitySafely(world, computer);
		if (computerTileEntity == null)
			return false;

		final IInventory inventory = StorageModuleUtils.getInventoryAtFace(line, computer, world, sideParam, functionName);
		if (inventory == null)
			return false;

		int inventoryIndex = getSpecifiedSlotIndex(line, computer, inventory, sideParam, slotParam, functionName);
		if (inventoryIndex == -1)
			return false;

		ItemStack stackInSlot = inventory.getStackInSlot(inventoryIndex);
		if (stackInSlot == null)
			return false;
		int toTransfer = Math.min(stackInSlot.stackSize, count);
		int transferred = StorageModuleUtils.mergeItemStackIntoComputerInventory(computerTileEntity, stackInSlot, toTransfer);
		inventory.decrStackSize(inventoryIndex, transferred);

		if (transferred > 0) {
			inventory.onInventoryChanged();
			computerTileEntity.onInventoryChanged();
		}

		return transferred == toTransfer;
	}

	private int getSpecifiedSlotIndex(int line, ServerComputerData computer, IInventory inventory, Variable sideParam, Variable slotParam, String functionName) throws ExecutionException {
		if (inventory instanceof ISidedInventory) {
			final ISidedInventory sidedInventory = (ISidedInventory) inventory;
			int inventorySide = BoxSide.getOpposite(StorageModuleUtils.getComputerFacingSide(line, computer, sideParam, functionName));
			final int[] sideSlots = sidedInventory.getSizeInventorySide(inventorySide);
			if (slotParam.getType() == Variable.Type.NULL) {
				for (int i = 0; i < sideSlots.length; i++) {
					final ItemStack stack = sidedInventory.getStackInSlot(sideSlots[i]);
					if (stack != null)
						return sideSlots[i];
				}
				return -1;
			} else {
				if (slotParam.getType() != Variable.Type.NUMBER)
					throw new ExecutionException(line, "Expected NUMBER in " + functionName + "()");

				int slot = ((Number) slotParam.getValue()).intValue();
				if (sideSlots.length <= slot || slot < 0)
					throw new ExecutionException(line, "Slot number out of accepted range in " + functionName + "()");

				return sideSlots[slot];
			}
		} else {
			int inventorySize = inventory.getSizeInventory();
			if (slotParam.getType() == Variable.Type.NULL) {
				for (int i = 0; i < inventorySize; i++) {
					final ItemStack stack = inventory.getStackInSlot(i);
					if (stack != null)
						return i;
				}
				return -1;
			} else {
				if (slotParam.getType() != Variable.Type.NUMBER)
					throw new ExecutionException(line, "Expected NUMBER in " + functionName + "()");

				int slot = ((Number) slotParam.getValue()).intValue();
				if (inventorySize <= slot || slot < 0)
					throw new ExecutionException(line, "Slot number out of accepted range in " + functionName + "()");

				return slot;
			}
		}
	}
}