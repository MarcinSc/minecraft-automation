package com.gempukku.minecraft.automation;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.ChunkEvent;

import java.io.File;
import java.util.Collection;

public class ServerAutomationProxy implements AutomationProxy {
    private ServerAutomationRegistry _automationRegistry;
    private ComputerProcessing _programProcessing;

    @Override
    public void initialize(File modConfigDirectory) {
        _automationRegistry = new ServerAutomationRegistry(modConfigDirectory);
        _programProcessing = new ComputerProcessing(modConfigDirectory, _automationRegistry);
        MinecraftForge.EVENT_BUS.register(_automationRegistry);
        MinecraftForge.EVENT_BUS.register(_programProcessing);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public ComputerProcessing getComputerProcessing() {
        return _programProcessing;
    }

    @Override
    public ServerAutomationRegistry getRegistry() {
        return _automationRegistry;
    }

    @ForgeSubscribe
    public void computersRemovedOnChunkUnload(ChunkEvent.Unload evt) {
        final Chunk chunk = evt.getChunk();
        Collection<TileEntity> tileEntities = chunk.chunkTileEntityMap.values();
        for (TileEntity tileEntity : tileEntities) {
            if (tileEntity instanceof ComputerTileEntity) {
                final ComputerTileEntity computerTileEntity = (ComputerTileEntity) tileEntity;
                MinecraftForge.EVENT_BUS.post(new ComputerEvent.ComputerRemovedFromWorldEvent(evt.world, _automationRegistry.getComputerData(computerTileEntity.getComputerId())));
            }
        }
    }

    @ForgeSubscribe
    public void computersAddedOnChunkLoad(ChunkEvent.Load evt) {
        final Chunk chunk = evt.getChunk();
        Collection<TileEntity> tileEntities = chunk.chunkTileEntityMap.values();
        for (TileEntity tileEntity : tileEntities) {
            if (tileEntity instanceof ComputerTileEntity) {
                final ComputerTileEntity computerTileEntity = (ComputerTileEntity) tileEntity;
                MinecraftForge.EVENT_BUS.post(new ComputerEvent.ComputerRemovedFromWorldEvent(evt.world, _automationRegistry.getComputerData(computerTileEntity.getComputerId())));
            }
        }
    }
}
