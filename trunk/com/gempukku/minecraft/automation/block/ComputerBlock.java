package com.gempukku.minecraft.automation.block;

import com.gempukku.minecraft.MinecraftUtils;
import com.gempukku.minecraft.automation.Automation;
import com.gempukku.minecraft.automation.AutomationUtils;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.computer.bind.ModuleComputerCallbackImpl;
import com.gempukku.minecraft.automation.computer.module.ComputerModule;
import com.gempukku.minecraft.automation.gui.computer.ComputerGuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

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

	public void breakBlock(World world, int x, int y, int z, int par5, int par6) {
		final ComputerTileEntity computerEntity = AutomationUtils.getComputerEntitySafely(world, x, y, z);

		if (computerEntity != null) {
			// Eject all items in computer's inventory.
			// The modules are not ejected - by design.
			for (int inventoryIndex = 0; inventoryIndex < computerEntity.getSizeInventory(); inventoryIndex++) {
				ItemStack itemstack = computerEntity.getStackInSlot(inventoryIndex);

				if (itemstack != null) {
					EntityItem entityItem = new EntityItem(world, x, y, z, new ItemStack(itemstack.itemID, itemstack.stackSize, itemstack.getItemDamage()));
					world.spawnEntityInWorld(entityItem);
					if (itemstack.hasTagCompound())
						entityItem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
				}
			}

			if (MinecraftUtils.isServer(world) && !computerEntity.isMoving())
				Automation.getServerProxy().getRegistry().unloadComputer(computerEntity);
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
			final short state = tileEntity.getState();
			if (state == ComputerTileEntity.STATE_RUNNING || state == ComputerTileEntity.STATE_SUSPENDED)
				return _frontWorkingIcon;
			else
				return _frontReadyIcon;
		}

		return _sideIcon;
	}

	@Override
	public int damageDropped(int par1) {
		return par1;
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
		if (tileEntity != null && MinecraftUtils.isServer((World) blockAccess)) {
			final ServerComputerData computerData = Automation.getServerProxy().getRegistry().getComputerData(tileEntity.getComputerId());
			int count = tileEntity.getModuleSlotsCount();
			int input = 0;
			for (int i = 0; i < count; i++) {
				final ComputerModule module = tileEntity.getModule(i);
				if (module != null)
					input = module.getStrongRedstoneSignalStrengthOnSide(new ModuleComputerCallbackImpl(blockAccess, i, computerData), input, blockAccess, side);
			}

			return input;
		}

		return super.isProvidingStrongPower(blockAccess, x, y, z, side);
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess blockAccess, int x, int y, int z, int side) {
		final ComputerTileEntity tileEntity = AutomationUtils.getComputerEntitySafely(blockAccess, x, y, z);
		if (tileEntity != null && MinecraftUtils.isServer((World) blockAccess)) {
			final ServerComputerData computerData = Automation.getServerProxy().getRegistry().getComputerData(tileEntity.getComputerId());
			int count = tileEntity.getModuleSlotsCount();
			int input = 0;
			for (int i = 0; i < count; i++) {
				final ComputerModule module = tileEntity.getModule(i);
				if (module != null)
					input = module.getWeakRedstoneSignalStrengthOnSide(new ModuleComputerCallbackImpl(blockAccess, i, computerData), input, blockAccess, side);
			}

			return input;
		}

		return super.isProvidingWeakPower(blockAccess, x, y, z, side);
	}

	public boolean canProvidePower() {
		return true;
	}
}
