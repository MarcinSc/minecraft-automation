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

public class TransferFromSelfFunction extends JavaFunctionExecutable {
	@Override
	protected int getDuration() {
		return 100;
	}

	@Override
	public String[] getParameterNames() {
		return new String[]{"side", "slot", "count"};
	}

	@Override
	protected Object executeFunction(World world, ServerComputerData computer, Map<String, Variable> parameters) throws ExecutionException {
		final Variable sideParam = parameters.get("side");
		final Variable slotParam = parameters.get("slot");
		final Variable countParam = parameters.get("count");

		int count;
		if (countParam.getType() == Variable.Type.NULL)
			count = Integer.MAX_VALUE;
		else if (countParam.getType() == Variable.Type.NUMBER)
			count = ((Number) countParam.getValue()).intValue();
		else
			throw new ExecutionException("Expected number or null in count parameter in transferFromSelf function");

		final String functionName = "transferFromSelf";

		final ComputerTileEntity computerTileEntity = AutomationUtils.getComputerEntitySafely(world, computer);
		if (computerTileEntity == null)
			return false;

		final int computerSlotIndex = getSpecifiedSlotIndex(computerTileEntity, slotParam, functionName);
		if (computerSlotIndex == -1)
			return false;

		final IInventory inventory = StorageModuleUtils.getInventoryAtFace(computer, world, sideParam, functionName);
		if (inventory == null)
			return false;

		final ItemStack stackInSlot = computerTileEntity.getStackInSlot(computerSlotIndex);
		if (stackInSlot == null)
			return false;
		int toTransfer = Math.min(stackInSlot.stackSize, count);
		int transferred = 0;
		int startFrom = 0;
		int inventorySlotIndex;
		// Try to merge the stack items into computer available slots
		while (transferred < toTransfer && (inventorySlotIndex = getFirstSlotOfSameTypeOrEmptyIndex(computer, inventory, sideParam, stackInSlot, startFrom, functionName)) != -1) {
			final ItemStack stackInInventory = inventory.getStackInSlot(inventorySlotIndex);
			int inventoryStackSize = (stackInInventory != null) ? stackInInventory.stackSize : 0;
			int availableSpace = (stackInInventory != null) ? stackInInventory.getMaxStackSize() - stackInInventory.stackSize : 64;
			int transferCount = Math.min(toTransfer - transferred, availableSpace);
			final ItemStack itemStack = computerTileEntity.decrStackSize(computerSlotIndex, transferCount);
			inventory.setInventorySlotContents(inventorySlotIndex, new ItemStack(itemStack.itemID, itemStack.stackSize + inventoryStackSize, itemStack.getItemDamage()));
			transferred += transferCount;
			startFrom = inventorySlotIndex + 1;
		}

		if (transferred > 0) {
			inventory.onInventoryChanged();
			computerTileEntity.onInventoryChanged();
		}

		return transferred == toTransfer;
	}

	private int getFirstSlotOfSameTypeOrEmptyIndex(ServerComputerData computer, IInventory inventory, Variable sideParam, ItemStack stack, int fromIndex, String functionName) throws ExecutionException {
		if (inventory instanceof ISidedInventory) {
			int inventorySide = BoxSide.getOpposite(StorageModuleUtils.getComputerFacingSide(computer, sideParam, functionName));
			int[] sideSlots = ((ISidedInventory) inventory).getSizeInventorySide(inventorySide);
			for (int i = fromIndex; i < sideSlots.length; i++) {
				final ItemStack stackInSlot = inventory.getStackInSlot(sideSlots[i]);
				if (stackInSlot == null || (stackInSlot.itemID == stack.itemID && stackInSlot.getItemDamage() == stack.getItemDamage()))
					return i;
			}
			return -1;
		} else {
			final int inventorySize = inventory.getSizeInventory();
			for (int i = fromIndex; i < inventorySize; i++) {
				final ItemStack stackInSlot = inventory.getStackInSlot(i);
				if (stackInSlot == null || (stackInSlot.itemID == stack.itemID && stackInSlot.getItemDamage() == stack.getItemDamage()))
					return i;
			}
			return -1;
		}
	}

	private int getSpecifiedSlotIndex(IInventory inventory, Variable slotParam, String functionName) throws ExecutionException {
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
				throw new ExecutionException("Expected number in slot parameter in " + functionName + " function");

			int slot = ((Number) slotParam.getValue()).intValue();
			if (inventorySize <= slot || slot < 0)
				throw new ExecutionException("Slot number out of accepted range in " + functionName + " function");

			return slot;
		}
	}
}
