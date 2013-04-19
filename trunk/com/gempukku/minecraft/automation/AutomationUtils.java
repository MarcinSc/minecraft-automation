package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.MinecraftUtils;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.io.File;

public class AutomationUtils {
	public static World getWorldComputerIsIn(ServerComputerData computerData) {
		return DimensionManager.getWorld(computerData.getId());
	}

	public static ComputerTileEntity getComputerEntitySafely(IBlockAccess blockAccess, ServerComputerData computerData) {
		return getComputerEntitySafely(blockAccess, computerData.getX(), computerData.getY(), computerData.getZ());
	}

	public static ComputerTileEntity getComputerEntitySafely(ServerComputerData computerData) {
		return getComputerEntitySafely(DimensionManager.getWorld(computerData.getDimension()), computerData.getX(), computerData.getY(), computerData.getZ());
	}

	public static ComputerTileEntity getComputerEntitySafely(IBlockAccess blockAccess, int x, int y, int z) {
		final TileEntity tileEntity = blockAccess.getBlockTileEntity(x, y, z);
		if (tileEntity != null && tileEntity instanceof ComputerTileEntity)
			return (ComputerTileEntity) tileEntity;
		return null;
	}

	public static File getComputerSavesFolder(File savesFolder, int computerId) {
		File worldFolder = new File(savesFolder, MinecraftUtils.getWorldNameOnServer());
		File automationFolder = new File(worldFolder, "automation");
		return new File(automationFolder, String.valueOf(computerId));
	}
}
