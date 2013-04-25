package com.gempukku.minecraft.automation.module.harvest;

import com.gempukku.minecraft.BoxSide;
import com.gempukku.minecraft.automation.AutomationUtils;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.JavaFunctionExecutable;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import com.gempukku.minecraft.automation.module.ComputerModuleUtils;
import com.gempukku.minecraft.automation.module.storage.StorageModuleUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Facing;
import net.minecraft.world.World;
import net.minecraftforge.common.FakePlayer;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.ArrayList;
import java.util.Map;

public class HarvestFunction extends JavaFunctionExecutable {
	@Override
	protected int getDuration() {
		return 10000;
	}

	@Override
	public String[] getParameterNames() {
		return new String[]{"direction"};
	}

	@Override
	protected Object executeFunction(int line, World world, ServerComputerData computer, Map<String, Variable> parameters) throws ExecutionException {
		final Variable directionVar = parameters.get("direction");
		if (directionVar.getType() != Variable.Type.STRING && directionVar.getType() != Variable.Type.NULL)
			throw new ExecutionException(line, "Invalid direction received in harvest()");

		String side = (String) directionVar.getValue();
		if (side != null && (!side.equals("up") || !side.equals("down")))
			throw new ExecutionException(line, "Invalid direction received in harvest()");

		int direction = computer.getFacing();
		if (side != null) {
			if (side.equals("up"))
				direction = BoxSide.TOP;
			else if (side.equals("down"))
				direction = BoxSide.BOTTOM;
		}

		final int harvestX = computer.getX() + Facing.offsetsXForSide[direction];
		final int harvestY = computer.getY() + Facing.offsetsYForSide[direction];
		final int harvestZ = computer.getZ() + Facing.offsetsZForSide[direction];

		if (!world.getChunkProvider().chunkExists(harvestX >> 4, harvestZ >> 4))
			return false;

		final Block block = Block.blocksList[world.getBlockId(harvestX, harvestY, harvestZ)];
		if (block == null)
			// Can't harvest air
			return false;

		final float blockHardness = block.getBlockHardness(world, harvestX, harvestY, harvestZ);

		final FakePlayer fakePlayer = new FakePlayer(world, computer.getOwner());
		final boolean canHarvest = ForgeEventFactory.doPlayerHarvestCheck(fakePlayer, block, blockHardness >= 0);
		if (!canHarvest)
			return false;

		final int blockMetadata = world.getBlockMetadata(harvestX, harvestY, harvestZ);

		final ComputerTileEntity computerTileEntity = AutomationUtils.getComputerEntitySafely(world, computer);
		if (computerTileEntity == null)
			return false;

		block.onBlockHarvested(world, harvestX, harvestY, harvestZ, blockMetadata, fakePlayer);
		block.removeBlockByPlayer(world, fakePlayer, harvestX, harvestY, harvestZ);

		final ArrayList<ItemStack> droppedItems = block.getBlockDropped(world, harvestX, harvestY, harvestZ, blockMetadata, 0);
		if (ComputerModuleUtils.canManipulateInventories(world, computer)) {
			for (ItemStack droppedItem : droppedItems) {
				final int transferred = StorageModuleUtils.mergeItemStackIntoComputerInventory(computerTileEntity, droppedItem, droppedItem.stackSize);
				if (transferred < droppedItem.stackSize)
					dropItemsOnGround(world, harvestX, harvestY, harvestZ, droppedItem.itemID, droppedItem.getItemDamage(), droppedItem.stackSize - transferred, droppedItem.getTagCompound());
			}
		} else {
			for (ItemStack droppedItem : droppedItems)
				dropItemsOnGround(world, harvestX, harvestY, harvestZ, droppedItem.itemID, droppedItem.getItemDamage(), droppedItem.stackSize, droppedItem.getTagCompound());
		}

		return true;
	}

	private void dropItemsOnGround(World world, int x, int y, int z, int itemId, int itemDamage, int count, NBTTagCompound tagCompound) {
		EntityItem entityItem = new EntityItem(world, x, y, z, new ItemStack(itemId, count, itemDamage));
		world.spawnEntityInWorld(entityItem);
		if (tagCompound != null)
			entityItem.getEntityItem().setTagCompound((NBTTagCompound) tagCompound.copy());
	}
}
