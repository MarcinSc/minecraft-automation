package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.MinecraftUtils;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.io.File;

public class AutomationUtils {
	public static World getWorldComputerIsIn(ServerComputerData computerData) {
		for (WorldServer worldServer : MinecraftServer.getServer().worldServers) {
			if (worldServer.provider.dimensionId == computerData.getDimension())
				return worldServer;
		}
		return null;
	}

	public static ComputerTileEntity getComputerEntitySafely(IBlockAccess blockAccess, ServerComputerData computerData) {
		return getComputerEntitySafely(blockAccess, computerData.getX(), computerData.getY(), computerData.getZ());
	}

	public static ComputerTileEntity getComputerEntitySafely(IBlockAccess blockAccess, int x, int y, int z) {
		final TileEntity tileEntity = blockAccess.getBlockTileEntity(x, y, z);
		if (tileEntity != null && tileEntity instanceof ComputerTileEntity)
			return (ComputerTileEntity) tileEntity;
		System.err.println("Unable to get ComputerTileEntity, stack trace follows");
		new Exception().fillInStackTrace().printStackTrace();
		return null;
	}

	public static File getComputerSavesFolder(File savesFolder, int computerId) {
		File worldFolder = new File(savesFolder, MinecraftUtils.getWorldNameOnServer());
		File automationFolder = new File(worldFolder, "automation");
		return new File(automationFolder, String.valueOf(computerId));
	}
}
