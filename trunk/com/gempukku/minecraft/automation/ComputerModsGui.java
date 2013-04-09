package com.gempukku.minecraft.automation;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;

public class ComputerModsGui extends GuiContainer {
    public ComputerModsGui(InventoryPlayer inventoryPlayer, ComputerTileEntity computerTileEntity) {
        super(new ComputerModsContainer(inventoryPlayer, computerTileEntity));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.func_98187_b("/gui/computerMods.png");
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }
}
