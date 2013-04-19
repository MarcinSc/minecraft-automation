package com.gempukku.minecraft;

import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class MinecraftUtils {
	public static boolean isClient(World world) {
		return world.isRemote;
	}

	public static boolean isServer(World world) {
		return !world.isRemote;
	}

	public static void setTileEntity(World world, int x, int y, int z, TileEntity tileEntity) {
		tileEntity.validate();
		world.setBlockTileEntity(x, y, z, tileEntity);
	}

	public static void updateTileEntity(World world, int x, int y, int z) {
		world.markBlockForUpdate(x, y, z);
	}

	public static String getWorldNameOnServer() {
		return MinecraftServer.getServer().getWorldName();
	}
}
