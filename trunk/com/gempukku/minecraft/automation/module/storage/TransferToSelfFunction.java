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
    protected Object executeFunction(ServerComputerData computer, World world, Map<String, Variable> parameters) throws ExecutionException {
        final Variable sideParam = parameters.get("side");
        final Variable slotParam = parameters.get("slot");
        final Variable countParam = parameters.get("count");

        int count;
        if (countParam.getType() == Variable.Type.NULL)
            count = Integer.MAX_VALUE;
        else if (countParam.getType() == Variable.Type.NUMBER)
            count = ((Number) countParam.getValue()).intValue();
        else
            throw new ExecutionException("Expected number or null in count parameter in transferToSelf function");

        final String functionName = "transferToSelf";

        final ComputerTileEntity computerTileEntity = AutomationUtils.getComputerEntitySafely(world, computer);
        if (computerTileEntity == null)
            return false;

        final IInventory inventory = StorageModuleUtils.getInventoryAtFace(computer, world, sideParam, functionName);
        if (inventory == null)
            return false;

        int inventoryIndex = getSpecifiedSlotIndex(computer, inventory, sideParam, slotParam, functionName);
        if (inventoryIndex == -1)
            return false;

        ItemStack stackInSlot = inventory.getStackInSlot(inventoryIndex);
        int toTransfer = Math.max(stackInSlot.stackSize, count);
        int transferred = 0;
        int startFrom = 0;
        int computerSlotIndex;
        // Try to merge the stack items into computer available slots
        while (transferred < toTransfer && (computerSlotIndex = getFirstSlotOfSameTypeOrEmptyIndex(computerTileEntity, stackInSlot, startFrom)) != -1) {
            final ItemStack stackInComputer = computerTileEntity.getStackInSlot(computerSlotIndex);
            int computerStackSize = (stackInComputer != null) ? stackInComputer.stackSize : 0;
            int availableSpace = (stackInComputer != null) ? stackInComputer.getMaxStackSize() - stackInComputer.stackSize : 64;
            int transferCount = Math.min(toTransfer - transferred, availableSpace);
            final ItemStack itemStack = inventory.decrStackSize(inventoryIndex, transferCount);
            computerTileEntity.setInventorySlotContents(computerSlotIndex, new ItemStack(itemStack.itemID, itemStack.stackSize + computerStackSize, itemStack.getItemDamage()));
            transferred += transferCount;
            startFrom = computerSlotIndex + 1;
        }

        if (transferred > 0) {
            inventory.onInventoryChanged();
            computerTileEntity.onInventoryChanged();
        }

        return transferred == toTransfer;
    }

    private int getFirstSlotOfSameTypeOrEmptyIndex(IInventory inventory, ItemStack stack, int fromIndex) {
        final int inventorySize = inventory.getSizeInventory();
        for (int i = fromIndex; i < inventorySize; i++) {
            final ItemStack stackInSlot = inventory.getStackInSlot(i);
            if (stackInSlot == null || (stackInSlot.itemID == stack.itemID && stackInSlot.getItemDamage() == stack.getItemDamage()))
                return i;
        }
        return -1;
    }

    private int getSpecifiedSlotIndex(ServerComputerData computer, IInventory inventory, Variable sideParam, Variable slotParam, String functionName) throws ExecutionException {
        if (inventory instanceof ISidedInventory) {
            final ISidedInventory sidedInventory = (ISidedInventory) inventory;
            int inventorySide = BoxSide.getOpposite(StorageModuleUtils.getComputerFacingSide(computer, sideParam, functionName));
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
                    throw new ExecutionException("Expected number in slot parameter in " + functionName + " function");

                int slot = ((Number) slotParam.getValue()).intValue();
                if (sideSlots.length <= slot || slot < 0)
                    throw new ExecutionException("Slot number out of accepted range in " + functionName + " function");

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
                    throw new ExecutionException("Expected number in slot parameter in " + functionName + " function");

                int slot = ((Number) slotParam.getValue()).intValue();
                if (inventorySize <= slot || slot < 0)
                    throw new ExecutionException("Slot number out of accepted range in " + functionName + " function");

                return slot;
            }
        }
    }
}