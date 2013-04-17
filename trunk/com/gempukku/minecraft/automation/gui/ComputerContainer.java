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
        int xModuleStart = (ComputerItemGuiBindings.WINDOW_WIDTH - 18 * moduleSlotsCount) / 2;
        for (int i = 0; i < moduleSlotsCount; i++)
            addSlotToContainer(new ComputerModuleSlot(tileEntity, i, xModuleStart + 18 * i, ComputerItemGuiBindings.MODULE_ROW_START));

        int xItemStart = 8;
        int yItemStart = ComputerItemGuiBindings.TOP_HEIGHT
                + ComputerItemGuiBindings.MODULE_ROW_HEIGHT
                + ComputerItemGuiBindings.MODULE_ROW_SPACER_HEIGHT
                + ComputerItemGuiBindings.COMPUTER_INVENTORY_HEIGHT;

        final int itemSlotsCount = tileEntity.getItemSlotsCount();
        for (int i = 0; i < itemSlotsCount; i++)
            addSlotToContainer(new Slot(tileEntity, moduleSlotsCount + i, xItemStart + 18 * (i % 9), yItemStart + 18 * (i / 9)));

        bindPlayerInventory(inventoryPlayer, (itemSlotsCount + 8) / 9);
    }

    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer, int itemSlotRows) {
        int xInventoryStart = 8;
        int yInventoryStart = ComputerItemGuiBindings.TOP_HEIGHT
                + ComputerItemGuiBindings.MODULE_ROW_HEIGHT
                + ComputerItemGuiBindings.MODULE_ROW_SPACER_HEIGHT;

        if (itemSlotRows > 0)
            yInventoryStart += ComputerItemGuiBindings.COMPUTER_INVENTORY_HEIGHT
                    + ComputerItemGuiBindings.COMPUTER_INVENTORY_ROW_HEIGHT * itemSlotRows
                    + ComputerItemGuiBindings.COMPUTER_INVENTORY_SPACER_HEIGHT;

        for (int index = 0; index < 27; index++)
            addSlotToContainer(new Slot(inventoryPlayer, 9 + index, xInventoryStart + 18 * (index % 9), yInventoryStart + 18 * (index / 9)));

        int xHotbarStart = 8;
        int yHotbarStart = yInventoryStart
                + ComputerItemGuiBindings.PLAYER_INVENTORY_ROW_HEIGHT * 3
                + ComputerItemGuiBindings.PLAYER_INVENTORY_SPACER_HEIGHT;

        for (int equippedColumn = 0; equippedColumn < 9; equippedColumn++)
            addSlotToContainer(new Slot(inventoryPlayer, equippedColumn, xHotbarStart + equippedColumn * 18, yHotbarStart));

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
