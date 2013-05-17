package com.gempukku.minecraft.automation.server;

import com.gempukku.minecraft.automation.CommonAutomationProxy;
import com.gempukku.minecraft.automation.program.ComputerProcessing;
import com.gempukku.minecraft.automation.program.ComputerSpeedProgramScheduler;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;

public class ServerAutomationProxy extends CommonAutomationProxy {
    private ServerAutomationRegistry _automationRegistry;
    private ComputerProcessing _programProcessing;

    @Override
    public void initialize(File modConfigDirectory) {
        File savesFolder = new File(modConfigDirectory.getParentFile(), "saves");
        _automationRegistry = new ServerAutomationRegistry(savesFolder);
        _programProcessing = new ComputerProcessing(savesFolder, _automationRegistry, new ComputerSpeedProgramScheduler());
        MinecraftForge.EVENT_BUS.register(_programProcessing);
    }

//	@ForgeSubscribe
//	public void chunkLoaded(ChunkEvent.Load evt) {
//		final Chunk chunk = evt.getChunk();
//		if (MinecraftUtils.isServer(chunk.worldObj)) {
//			final ServerAutomationRegistry serverRegistry = Automation.getServerProxy().getRegistry();
//			for (TileEntity tileEntity : (Collection<TileEntity>) chunk.chunkTileEntityMap.values()) {
//				if (tileEntity instanceof ComputerTileEntity)
//					serverRegistry.ensureComputerLoaded((ComputerTileEntity) tileEntity);
//			}
//		}
//	}
//
//	@ForgeSubscribe
//	public void chunkUnloaded(ChunkEvent.Unload evt) {
//		final Chunk chunk = evt.getChunk();
//		if (MinecraftUtils.isServer(chunk.worldObj)) {
//			final ServerAutomationRegistry serverRegistry = Automation.getServerProxy().getRegistry();
//			for (TileEntity tileEntity : (Collection<TileEntity>) chunk.chunkTileEntityMap.values()) {
//				if (tileEntity instanceof ComputerTileEntity)
//					serverRegistry.unloadComputer((ComputerTileEntity) tileEntity);
//			}
//		}
//	}
//
//	@ForgeSubscribe
//	public void worldUnload(WorldEvent.Unload evt) {
//		if (MinecraftUtils.isServer(evt.world)) {
//			final ServerAutomationRegistry serverRegistry = Automation.getServerProxy().getRegistry();
//			serverRegistry.unloadComputersFromDimension(evt.world.provider.dimensionId);
//		}
//	}

    public ComputerProcessing getComputerProcessing() {
        return _programProcessing;
    }

    @Override
    public ServerAutomationRegistry getRegistry() {
        return _automationRegistry;
    }
}
