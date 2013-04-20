package com.gempukku.minecraft.automation.server;

import com.gempukku.minecraft.MinecraftUtils;
import com.gempukku.minecraft.automation.Automation;
import com.gempukku.minecraft.automation.CommonAutomationProxy;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.program.ComputerProcessing;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;

import java.io.File;
import java.util.Collection;

public class ServerAutomationProxy extends CommonAutomationProxy {
	private ServerAutomationRegistry _automationRegistry;
	private ComputerProcessing _programProcessing;

	@Override
	public void initialize(File modConfigDirectory) {
		File savesFolder = new File(modConfigDirectory.getParentFile(), "saves");
		_automationRegistry = new ServerAutomationRegistry(savesFolder);
		_programProcessing = new ComputerProcessing(savesFolder, _automationRegistry);
		MinecraftForge.EVENT_BUS.register(_programProcessing);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@ForgeSubscribe
	public void chunkLoaded(ChunkEvent.Load evt) {
		final Chunk chunk = evt.getChunk();
		if (MinecraftUtils.isServer(chunk.worldObj)) {
			final ServerAutomationRegistry serverRegistry = Automation.getServerProxy().getRegistry();
			for (TileEntity tileEntity : (Collection<TileEntity>) chunk.chunkTileEntityMap.values()) {
				if (tileEntity instanceof ComputerTileEntity)
					serverRegistry.ensureComputerLoaded((ComputerTileEntity) tileEntity);
			}
		}
	}

	@ForgeSubscribe
	public void chunkUnloaded(ChunkEvent.Unload evt) {
		final Chunk chunk = evt.getChunk();
		if (MinecraftUtils.isServer(chunk.worldObj)) {
			final ServerAutomationRegistry serverRegistry = Automation.getServerProxy().getRegistry();
			for (TileEntity tileEntity : (Collection<TileEntity>) chunk.chunkTileEntityMap.values()) {
				if (tileEntity instanceof ComputerTileEntity)
					serverRegistry.unloadComputer((ComputerTileEntity) tileEntity);
			}
		}
	}

	@ForgeSubscribe
	public void worldUnload(WorldEvent.Unload evt) {
		if (MinecraftUtils.isServer(evt.world)) {
			final ServerAutomationRegistry serverRegistry = Automation.getServerProxy().getRegistry();
			serverRegistry.unloadComputersFromDimension(evt.world.provider.dimensionId);
		}
	}

	public ComputerProcessing getComputerProcessing() {
		return _programProcessing;
	}

	@Override
	public ServerAutomationRegistry getRegistry() {
		return _automationRegistry;
	}
}
