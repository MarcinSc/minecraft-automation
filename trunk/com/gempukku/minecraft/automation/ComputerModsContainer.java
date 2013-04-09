package com.gempukku.minecraft.automation;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;

public class ComputerModsContainer extends Container {
    private ComputerTileEntity _tileEntity;

    public ComputerModsContainer(InventoryPlayer inventoryPlayer, ComputerTileEntity tileEntity) {
        _tileEntity = tileEntity;
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return false;
    }
}
