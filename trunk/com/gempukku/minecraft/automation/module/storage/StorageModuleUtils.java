package com.gempukku.minecraft.automation.module.storage;

import com.gempukku.minecraft.BoxSide;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Facing;
import net.minecraft.world.World;

public class StorageModuleUtils {
	public static TileEntity getBlockEntityAtFace(int line, ServerComputerData computer, World world, Variable sideVar, String functionName) throws ExecutionException {
		int lookAt = getComputerFacingSide(line, computer, sideVar, functionName);

		return world.getBlockTileEntity(
						computer.getX() + Facing.offsetsXForSide[lookAt],
						computer.getY() + Facing.offsetsYForSide[lookAt],
						computer.getZ() + Facing.offsetsZForSide[lookAt]);
	}

	public static IInventory getInventoryAtFace(int line, ServerComputerData computer, World world, Variable sideVar, String functionName) throws ExecutionException {
		final TileEntity tileEntity = getBlockEntityAtFace(line, computer, world, sideVar, functionName);
		return (tileEntity instanceof IInventory) ? (IInventory) tileEntity : null;
	}

	public static ItemStack getStackFromInventory(int line, ServerComputerData computer, IInventory inventory, Variable sideParam, Variable slotParam, String functionName) throws ExecutionException {
		if (slotParam.getType() != Variable.Type.NUMBER)
			throw new ExecutionException(line, "Expected number in slot parameter in " + functionName + "()");

		int slot = ((Number) slotParam.getValue()).intValue();

		int inventorySide = BoxSide.getOpposite(StorageModuleUtils.getComputerFacingSide(line, computer, sideParam, functionName));
		int inventorySize = getInventorySize(inventory, inventorySide);

		if (inventorySize <= slot || slot < 0)
			throw new ExecutionException(line, "Slot number out of accepted range in " + functionName + "()");

		ItemStack stackInSlot;
		if (inventory instanceof ISidedInventory) {
			stackInSlot = inventory.getStackInSlot(((ISidedInventory) inventory).getSizeInventorySide(inventorySide)[slot]);
		} else
			stackInSlot = inventory.getStackInSlot(slot);
		return stackInSlot;
	}

	public static int getInventorySize(IInventory inventory, int inventorySide) throws ExecutionException {
		if (inventory instanceof ISidedInventory) {
			return ((ISidedInventory) inventory).getSizeInventorySide(inventorySide).length;
		} else
			return inventory.getSizeInventory();
	}

	public static int getComputerFacingSide(int line, ServerComputerData computer, Variable sideVar, String functionName) throws ExecutionException {
		int facing = computer.getFacing();

		if (sideVar.getType() != Variable.Type.STRING)
			throw new ExecutionException(line, "Expected front, top, bottom, left or right in " + functionName + "()");

		String side = (String) sideVar.getValue();
		if (!side.equals("front") && !side.equals("left") && !side.equals("right") && !side.equals("top") && !side.equals("bottom"))
			throw new ExecutionException(line, "Expected front, top, bottom, left or right in " + functionName + "()");

		int lookAt = facing;
		if (side.equals("left"))
			lookAt = BoxSide.getLeft(facing);
		else if (side.equals("right"))
			lookAt = BoxSide.getRight(facing);
		else if (side.equals("top"))
			lookAt = BoxSide.BOTTOM;
		else if (side.equals("bottom"))
			lookAt = BoxSide.TOP;
		return lookAt;
	}
}
