package com.gempukku.minecraft.automation.gui;

import com.gempukku.minecraft.automation.Automation;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ComputerModuleSlot extends Slot {
    public ComputerModuleSlot(IInventory inventory, int slotNo, int x, int y) {
        super(inventory, slotNo, x, y);
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }

    @Override
    public boolean isItemValid(ItemStack itemStack) {
        return Automation.proxy.getRegistry().getModuleByItemId(itemStack.itemID, itemStack.getItemDamage()) != null;
    }
}
