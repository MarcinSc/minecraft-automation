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
//draw your Gui here, only thing you need to change is the path
        int texture = mc.renderEngine.getTexture("/gui/computerMods.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(texture);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }
}
