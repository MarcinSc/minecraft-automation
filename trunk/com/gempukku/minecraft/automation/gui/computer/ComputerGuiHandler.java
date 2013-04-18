package com.gempukku.minecraft.automation.gui.computer;

import com.gempukku.minecraft.automation.AutomationUtils;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.gui.computer.console.ComputerConsoleContainerOnServer;
import com.gempukku.minecraft.automation.gui.computer.console.ComputerConsoleGui;
import com.gempukku.minecraft.automation.gui.computer.item.ComputerItemContainer;
import com.gempukku.minecraft.automation.gui.computer.item.ComputerItemGui;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ComputerGuiHandler implements IGuiHandler {
    public static final int COMPUTER_PROGRAMMING_GUI = 0;
    public static final int COMPUTER_ITEM_GUI = 1;

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        ComputerTileEntity tileEntity = AutomationUtils.getComputerEntitySafely(world, x, y, z);
        if (tileEntity == null)
            return null;

        if (id == COMPUTER_ITEM_GUI)
            return new ComputerItemGui(player.inventory, tileEntity);
        else if (id == COMPUTER_PROGRAMMING_GUI)
            return new ComputerConsoleGui(player);
        return null;
    }

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        ComputerTileEntity tileEntity = AutomationUtils.getComputerEntitySafely(world, x, y, z);
        if (tileEntity == null)
            return null;

        if (id == COMPUTER_ITEM_GUI)
            return new ComputerItemContainer(player.inventory, tileEntity);
        else if (id == COMPUTER_PROGRAMMING_GUI)
            return new ComputerConsoleContainerOnServer(player, tileEntity);
        return null;
    }
}
