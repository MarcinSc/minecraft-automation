package com.gempukku.minecraft.automation.gui;

import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import org.lwjgl.opengl.GL11;

public class ComputerItemGui extends GuiContainer {
    public ComputerItemGui(InventoryPlayer inventoryPlayer, ComputerTileEntity computerTileEntity) {
        super(new ComputerContainer(inventoryPlayer, computerTileEntity));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture("/gui/computerMods.png");
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }

    @Override
    protected void handleMouseClick(Slot par1Slot, int par2, int par3, int par4) {
        super.handleMouseClick(par1Slot, par2, par3, par4);
    }
}
