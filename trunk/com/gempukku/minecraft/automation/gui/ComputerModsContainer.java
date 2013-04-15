package com.gempukku.minecraft.automation.gui;

import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public class ComputerModsContainer extends Container {
    private ComputerTileEntity _tileEntity;

    public ComputerModsContainer(InventoryPlayer inventoryPlayer, ComputerTileEntity tileEntity) {
        _tileEntity = tileEntity;

        final int moduleSlotsCount = tileEntity.getModuleSlotsCount();
        for (int i = 0; i < moduleSlotsCount; i++)
            addSlotToContainer(new Slot(tileEntity, i, 18 * i, 0));

        bindPlayerInventory(inventoryPlayer);
    }

    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
        for (int row = 0; row < 3; row++)
            for (int column = 0; column < 9; column++) {
                final int slotIndexInInventory = column + row * 9 + 9;
                addSlotToContainer(new Slot(inventoryPlayer, slotIndexInInventory,
                        8 + column * 18, 84 + row * 18));
            }

        for (int equippedColumn = 0; equippedColumn < 9; equippedColumn++)
            addSlotToContainer(new Slot(inventoryPlayer, equippedColumn, 8 + equippedColumn * 18, 142));

    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return _tileEntity.isUseableByPlayer(entityplayer);
    }
}
