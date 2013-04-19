package com.gempukku.minecraft.automation.block;

import com.gempukku.minecraft.MinecraftUtils;
import com.gempukku.minecraft.automation.Automation;
import com.gempukku.minecraft.automation.AutomationUtils;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.gui.computer.ComputerGuiHandler;
import com.gempukku.minecraft.automation.module.ComputerModule;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;

public abstract class ComputerBlock extends BlockContainer {
	private Icon _frontWorkingIcon;
	private Icon _frontReadyIcon;
	private Icon _sideIcon;
	private String _computerType;
	private int _moduleSlotCount;

	public ComputerBlock(int id, String computerType, int moduleSlotCount) {
		super(id, Material.ground);
		_computerType = computerType;
		_moduleSlotCount = moduleSlotCount;
		setHardness(1.5F);
		setResistance(10.0F);
		setUnlocalizedName("computer");
		setCreativeTab(CreativeTabs.tabBlock);
	}

	public abstract String getComputerFrontReadyIcon();

	protected abstract String getComputerFrontWorkingIcon();

	protected abstract String getComputerSideIcon();

	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		ComputerTileEntity computerTileEntity = AutomationUtils.getComputerEntitySafely(world, x, y, z);
		if (computerTileEntity != null) {
			dropBlockAsItem_do(world, x, y, z, new ItemStack(this, 1, computerTileEntity.getComputerId()));
			if (MinecraftUtils.isServer(world))
				Automation.getServerProxy().getRegistry().unloadComputer(computerTileEntity);
		}

		super.breakBlock(world, x, y, z, par5, par6);
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		_frontReadyIcon = iconRegister.registerIcon(getComputerFrontReadyIcon());
		_frontWorkingIcon = iconRegister.registerIcon(getComputerFrontWorkingIcon());
		_sideIcon = iconRegister.registerIcon(getComputerSideIcon());
	}

	@Override
	public Icon getBlockTexture(IBlockAccess blockAccess, int x, int y, int z, int side) {
		final ComputerTileEntity tileEntity = AutomationUtils.getComputerEntitySafely(blockAccess, x, y, z);
		if (tileEntity != null && side == tileEntity.getFacing()) {
			if (tileEntity.isRunningProgram())
				return _frontWorkingIcon;
			else
				return _frontReadyIcon;
		}

		return _sideIcon;
	}

	@Override
	public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
		// We already dropped the items in breakBlock method, therefore not dropping here anything
		return new ArrayList<ItemStack>();
	}

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new ComputerTileEntity();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float xPos, float yPos, float zPos) {
		if (player.isSneaking())
			return false;

		if (MinecraftUtils.isServer(world)) {
			final ItemStack heldItem = player.getHeldItem();
			if (heldItem != null && heldItem.itemID == Automation.terminalItem.itemID)
				player.openGui(Automation.instance, ComputerGuiHandler.COMPUTER_PROGRAMMING_GUI, world, x, y, z);
			else
				player.openGui(Automation.instance, ComputerGuiHandler.COMPUTER_ITEM_GUI, world, x, y, z);
		}

		return true;
	}

	public void initializeBlockAfterPlaced(World world, int x, int y, int z, int computerId, String playerPlacing, int blockFacing) {
		ComputerTileEntity computerEntity = populateTileEntityAfterPlacing(world, computerId, playerPlacing, blockFacing);
		MinecraftUtils.setTileEntity(world, x, y, z, computerEntity);
		if (MinecraftUtils.isServer(world))
			Automation.getServerProxy().getRegistry().ensureComputerLoaded(computerEntity);
	}

	private ComputerTileEntity populateTileEntityAfterPlacing(World world, int computerId, String playerPlacing, int blockFacing) {
		ComputerTileEntity result = new ComputerTileEntity();
		// If it's a new computer, on the server we have to assign an id to it
		if (computerId == 0 && MinecraftUtils.isServer(world))
			computerId = Automation.getServerProxy().getRegistry().storeNewComputerData(playerPlacing, _computerType);

		result.setModuleSlotsCount(_moduleSlotCount);
		// On the client we have to forget the label for this computer, as it might change after it's placed
		if (!MinecraftUtils.isClient(world))
			Automation.getClientProxy().getRegistry().clearLabelCache(computerId);
		result.setComputerId(computerId);
		result.setFacing(blockFacing);
		return result;
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess blockAccess, int x, int y, int z, int side) {
		final ComputerTileEntity tileEntity = AutomationUtils.getComputerEntitySafely(blockAccess, x, y, z);
		if (tileEntity != null) {
			final ServerComputerData computerData = Automation.getServerProxy().getRegistry().getComputerData(tileEntity.getComputerId());
			int count = tileEntity.getModuleSlotsCount();
			int input = 0;
			for (int i = 0; i < count; i++) {
				final ComputerModule module = tileEntity.getModule(i);
				if (module != null)
					input = module.getStrongRedstoneSignalStrengthOnSide(computerData, input, blockAccess, side);
			}

			return input;
		}

		return super.isProvidingStrongPower(blockAccess, x, y, z, side);
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess blockAccess, int x, int y, int z, int side) {
		final ComputerTileEntity tileEntity = AutomationUtils.getComputerEntitySafely(blockAccess, x, y, z);
		if (tileEntity != null) {
			final ServerComputerData computerData = Automation.getServerProxy().getRegistry().getComputerData(tileEntity.getComputerId());
			int count = tileEntity.getModuleSlotsCount();
			int input = 0;
			for (int i = 0; i < count; i++) {
				final ComputerModule module = tileEntity.getModule(i);
				if (module != null)
					input = module.getWeakRedstoneSignalStrengthOnSide(computerData, input, blockAccess, side);
			}

			return input;
		}

		return super.isProvidingWeakPower(blockAccess, x, y, z, side);
	}

	public boolean canProvidePower() {
		return true;
	}
}
