package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.MinecraftUtils;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.server.ServerAutomationRegistry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;

import java.util.Collection;

public abstract class CommonAutomationProxy implements AutomationProxy {
	@ForgeSubscribe
	public void chunkLoaded(ChunkEvent.Load evt) {
		final ServerAutomationRegistry serverRegistry = Automation.getServerProxy().getRegistry();
		final Chunk chunk = evt.getChunk();
		if (MinecraftUtils.isServer(chunk.worldObj))
			for (TileEntity tileEntity : (Collection<TileEntity>) chunk.chunkTileEntityMap.values()) {
				if (tileEntity instanceof ComputerTileEntity)
					serverRegistry.ensureComputerLoaded((ComputerTileEntity) tileEntity);
			}
	}

	@ForgeSubscribe
	public void chunkUnloaded(ChunkEvent.Unload evt) {
		final ServerAutomationRegistry serverRegistry = Automation.getServerProxy().getRegistry();
		final Chunk chunk = evt.getChunk();
		if (MinecraftUtils.isServer(chunk.worldObj))
			for (TileEntity tileEntity : (Collection<TileEntity>) chunk.chunkTileEntityMap.values()) {
				if (tileEntity instanceof ComputerTileEntity)
					serverRegistry.unloadComputer((ComputerTileEntity) tileEntity);
			}
	}

	@ForgeSubscribe
	public void worldUnload(WorldEvent.Unload evt) {
		final ServerAutomationRegistry serverRegistry = Automation.getServerProxy().getRegistry();
		if (MinecraftUtils.isServer(evt.world))
			serverRegistry.unloadComputersFromDimension(evt.world.getWorldInfo().getDimension());
	}
}
