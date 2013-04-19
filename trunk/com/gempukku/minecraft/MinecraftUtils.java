package com.gempukku.minecraft;

import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.network.packet.Packet;
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

	public static void sendTileEntityUpdateToPlayers(World world, TileEntity tileEntity) {
		final Packet descriptionPacket = tileEntity.getDescriptionPacket();
		if (descriptionPacket != null)
			PacketDispatcher.sendPacketToAllAround(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, 64d, world.getWorldInfo().getDimension(), descriptionPacket);
	}

	public static String getWorldNameOnServer() {
		return MinecraftServer.getServer().getWorldName();
	}
}
