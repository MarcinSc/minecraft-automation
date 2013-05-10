package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.MinecraftUtils;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.ComputerCallback;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.IBlockAccess;

import java.io.File;

public class AutomationUtils {
	public static ComputerTileEntity getComputerEntitySafely(IBlockAccess blockAccess, ComputerCallback computerData) {
		final ChunkPosition chunkPosition = computerData.getChunkPosition();
		return getComputerEntitySafely(blockAccess, chunkPosition.x, chunkPosition.y, chunkPosition.z);
	}

	public static ComputerTileEntity getComputerEntitySafely(IBlockAccess blockAccess, int x, int y, int z) {
		final TileEntity tileEntity = blockAccess.getBlockTileEntity(x, y, z);
		if (tileEntity != null && tileEntity instanceof ComputerTileEntity)
			return (ComputerTileEntity) tileEntity;
		System.err.println("Unable to get ComputerTileEntity, stack trace follows");
		new Exception().fillInStackTrace().printStackTrace();
		return null;
	}

    public static File getAutomationSavesFolder(File savesFolder) {
        File worldFolder = new File(savesFolder, MinecraftUtils.getWorldNameOnServer());
        return new File(worldFolder, "automation");
    }

	public static File getComputerSavesFolder(File savesFolder, int computerId) {
        File automationFolder = getAutomationSavesFolder(savesFolder);
		return new File(automationFolder, String.valueOf(computerId));
	}
}
