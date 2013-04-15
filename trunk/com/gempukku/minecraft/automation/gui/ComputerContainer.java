package com.gempukku.minecraft.automation.gui;

import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

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
}
