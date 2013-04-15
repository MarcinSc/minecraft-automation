package com.gempukku.minecraft.automation.gui;

import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ComputerContainer extends Container {
    private ComputerTileEntity _tileEntity;

    public ComputerContainer(InventoryPlayer inventoryPlayer, ComputerTileEntity tileEntity) {
        _tileEntity = tileEntity;

        final int moduleSlotsCount = tileEntity.getModuleSlotsCount();
        for (int i = 0; i < moduleSlotsCount; i++)
            addSlotToContainer(new ComputerModuleSlot(tileEntity, i, 18 * i, 0));

        final int itemSlotsCount = tileEntity.getItemSlotsCount();
        for (int i = 0; i < itemSlotsCount; i++)
            addSlotToContainer(new Slot(tileEntity, moduleSlotsCount + i, 18 * (i % 9), 18 * (i / 9)));

        bindPlayerInventory(inventoryPlayer);
    }

    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
        for (int index = 0; index < 27; index++)
            addSlotToContainer(new Slot(inventoryPlayer, 9 + index, 8 + 18 * (index % 9), 84 + 18 * (index / 9)));

        for (int equippedColumn = 0; equippedColumn < 9; equippedColumn++)
            addSlotToContainer(new Slot(inventoryPlayer, equippedColumn, 8 + equippedColumn * 18, 142));

    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return _tileEntity.isUseableByPlayer(entityplayer);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
        ItemStack stack = null;
        Slot slotObject = (Slot) inventorySlots.get(slot);

        //null checks and checks if the item can be stacked (maxStackSize > 1)
        if (slotObject != null && slotObject.getHasStack()) {
            ItemStack stackInSlot = slotObject.getStack();
            stack = stackInSlot.copy();

            //merges the item into player inventory since its in the tileEntity
            if (slot < 9) {
                if (!this.mergeItemStack(stackInSlot, 9, 36, true)) {
                    return null;
                }
            }
            //places it into the tileEntity is possible since its in the player inventory
            else if (!this.mergeItemStack(stackInSlot, 0, 9, false)) {
                return null;
            }

            if (stackInSlot.stackSize == 0) {
                slotObject.putStack(null);
            } else {
                slotObject.onSlotChanged();
            }

            if (stackInSlot.stackSize == stack.stackSize) {
                return null;
            }
            slotObject.onPickupFromSlot(player, stackInSlot);
        }
        return stack;
    }
}
