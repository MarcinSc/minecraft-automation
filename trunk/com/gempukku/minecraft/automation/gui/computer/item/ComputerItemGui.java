package com.gempukku.minecraft.automation.gui.computer.item;

import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import org.lwjgl.opengl.GL11;

public class ComputerItemGui extends GuiContainer {
	private int _computerItemSlotRows;
	private int _moduleSlotsCount;

	public ComputerItemGui(InventoryPlayer inventoryPlayer, ComputerTileEntity computerTileEntity) {
		super(new ComputerItemContainer(inventoryPlayer, computerTileEntity));
		_computerItemSlotRows = (computerTileEntity.getItemSlotsCount() + 8) / 9;
		_moduleSlotsCount = computerTileEntity.getModuleSlotsCount();

		xSize = ComputerItemGuiBindings.WINDOW_WIDTH;
		int height = ComputerItemGuiBindings.TOP_HEIGHT + ComputerItemGuiBindings.MODULE_ROW_HEIGHT;
		if (_computerItemSlotRows > 0)
			height += ComputerItemGuiBindings.COMPUTER_INVENTORY_HEIGHT
							+ ComputerItemGuiBindings.COMPUTER_INVENTORY_ROW_HEIGHT * _computerItemSlotRows
							+ ComputerItemGuiBindings.COMPUTER_INVENTORY_SPACER_HEIGHT;
		height += ComputerItemGuiBindings.PLAYER_INVENTORY_ROW_HEIGHT * 3
						+ ComputerItemGuiBindings.PLAYER_INVENTORY_SPACER_HEIGHT
						+ ComputerItemGuiBindings.PLAYER_HOTBAR_HEIGHT
						+ ComputerItemGuiBindings.BOTTOM_HEIGHT;
		ySize = height;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float param1, int param2, int param3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture("/mods/automation/gui/modulesGui.png");
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;

		int yPos = 0;
		this.drawTexturedModalRect(x, y, 0, ComputerItemGuiBindings.TOP_START, xSize, ComputerItemGuiBindings.TOP_HEIGHT);
		yPos += ComputerItemGuiBindings.TOP_HEIGHT;

		this.drawTexturedModalRect(x, y + yPos, 0, ComputerItemGuiBindings.MODULE_ROW_START, xSize, ComputerItemGuiBindings.MODULE_ROW_HEIGHT);

		int xModuleStart = (ComputerItemGuiBindings.WINDOW_WIDTH - 18 * _moduleSlotsCount) / 2;
		for (int i = 0; i < _moduleSlotsCount; i++)
			this.drawTexturedModalRect(x + xModuleStart + 18 * i - 1, y + yPos + 1, 200, 0, 18, 18);
//			addSlotToContainer(new ComputerModuleSlot(moduleInventory, i, xModuleStart + 18 * i, ComputerItemGuiBindings.MODULE_ROW_START));
		yPos += ComputerItemGuiBindings.MODULE_ROW_HEIGHT;


		if (_computerItemSlotRows > 0) {
			this.drawTexturedModalRect(x, y + yPos, 0, ComputerItemGuiBindings.COMPUTER_INVENTORY_START, xSize, ComputerItemGuiBindings.COMPUTER_INVENTORY_HEIGHT);
			yPos += ComputerItemGuiBindings.COMPUTER_INVENTORY_HEIGHT;
			for (int row = 0; row < _computerItemSlotRows; row++) {
				this.drawTexturedModalRect(x, y + yPos, 0, ComputerItemGuiBindings.COMPUTER_INVENTORY_ROW_START, xSize, ComputerItemGuiBindings.COMPUTER_INVENTORY_ROW_HEIGHT);
				yPos += ComputerItemGuiBindings.COMPUTER_INVENTORY_ROW_HEIGHT;
			}
			this.drawTexturedModalRect(x, y + yPos, 0, ComputerItemGuiBindings.COMPUTER_INVENTORY_SPACER_START, xSize, ComputerItemGuiBindings.COMPUTER_INVENTORY_SPACER_HEIGHT);
			yPos += ComputerItemGuiBindings.COMPUTER_INVENTORY_SPACER_HEIGHT;
		}
		for (int row = 0; row < 3; row++) {
			this.drawTexturedModalRect(x, y + yPos, 0, ComputerItemGuiBindings.PLAYER_INVENTORY_ROW_START, xSize, ComputerItemGuiBindings.PLAYER_INVENTORY_ROW_HEIGHT);
			yPos += ComputerItemGuiBindings.PLAYER_INVENTORY_ROW_HEIGHT;
		}
		this.drawTexturedModalRect(x, y + yPos, 0, ComputerItemGuiBindings.PLAYER_INVENTORY_SPACER_START, xSize, ComputerItemGuiBindings.PLAYER_INVENTORY_SPACER_HEIGHT);
		yPos += ComputerItemGuiBindings.PLAYER_INVENTORY_SPACER_HEIGHT;
		this.drawTexturedModalRect(x, y + yPos, 0, ComputerItemGuiBindings.PLAYER_HOTBAR_START, xSize, ComputerItemGuiBindings.PLAYER_HOTBAR_HEIGHT);
		yPos += ComputerItemGuiBindings.PLAYER_HOTBAR_HEIGHT;
		this.drawTexturedModalRect(x, y + yPos, 0, ComputerItemGuiBindings.BOTTOM_START, xSize, ComputerItemGuiBindings.BOTTOM_HEIGHT);
	}

	@Override
	protected void handleMouseClick(Slot par1Slot, int par2, int par3, int par4) {
		super.handleMouseClick(par1Slot, par2, par3, par4);
	}
}
