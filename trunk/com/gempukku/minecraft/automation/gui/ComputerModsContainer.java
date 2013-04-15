package com.gempukku.minecraft.automation.gui;

import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public class ComputerModsContainer extends Container {
    private ComputerTileEntity _tileEntity;
    private ServerComputerData _computerData;

    public ComputerModsContainer(InventoryPlayer inventoryPlayer, ComputerTileEntity tileEntity) {
        _tileEntity = tileEntity;

        final int moduleSlotsCount = tileEntity.getModuleSlotsCount();
        for (int i = 0; i < moduleSlotsCount; i++)
            addSlotToContainer(new Slot(tileEntity, i, 0, 16 * i));

        bindPlayerInventory(inventoryPlayer);
    }

    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9,
                        8 + j * 18, 84 + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return _tileEntity.isUseableByPlayer(entityplayer);
    }
}
