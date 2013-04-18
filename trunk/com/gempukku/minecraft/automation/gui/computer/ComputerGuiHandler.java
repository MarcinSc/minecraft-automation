package com.gempukku.minecraft.automation.gui.computer;

import com.gempukku.minecraft.automation.AutomationUtils;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.gui.computer.item.ComputerItemContainer;
import com.gempukku.minecraft.automation.gui.computer.item.ComputerItemGui;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ComputerGuiHandler implements IGuiHandler {
    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        ComputerTileEntity tileEntity = AutomationUtils.getComputerEntitySafely(world, x, y, z);
        if (tileEntity == null)
            return null;

        if (id == 1)
            return new ComputerItemGui(player.inventory, tileEntity);
        return null;
    }

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        ComputerTileEntity tileEntity = AutomationUtils.getComputerEntitySafely(world, x, y, z);
        if (tileEntity == null)
            return null;

        if (id == 1)
            return new ComputerItemContainer(player.inventory, tileEntity);
        return null;
    }
}
