package com.gempukku.minecraft.automation;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ComputerGuiHandler implements IGuiHandler {
    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == 1)
            return new ComputerModsGui(player.inventory, (ComputerTileEntity) world.getBlockTileEntity(x, y, z));
        return null;
    }

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == 1)
            return new ComputerModsContainer(player.inventory, (ComputerTileEntity) world.getBlockTileEntity(x, y, z));
        return null;
    }
}
