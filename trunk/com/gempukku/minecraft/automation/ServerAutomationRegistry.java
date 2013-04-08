package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.automation.computer.ComputerData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.ChunkEvent;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ServerAutomationRegistry implements AutomationRegistry {
    private Map<Integer, ComputerData> _computerDataMap = new HashMap<Integer, ComputerData>();
    private File _configFolder;
    private int _nextId;

    public ServerAutomationRegistry(File configFolder) {
        _configFolder = configFolder;
    }

    @Override
    public String getComputerLabel(int computerId) {
        return _computerDataMap.get(computerId).getLabel();
    }

    public int assignNextComputerId() {
        int result = _nextId;
        _nextId++;
        return result;
    }

    @Override
    public ComputerData getComputerData(int computerId) {
        return _computerDataMap.get(computerId);
    }

    private ComputerData readComputerDataFromDisk(int computerId) {
        File computerDataFile = getComputerDataFile(computerId);

        Properties properties = new Properties();
        try {
            FileReader reader = new FileReader(computerDataFile);
            try {
                properties.load(reader);
            } finally {
                reader.close();
            }
        } catch (IOException exp) {
            // TODO
        }

        int id = Integer.parseInt((String) properties.get("id"));
        String owner = (String) properties.get("owner");
        ComputerData computerData = new ComputerData(id, owner);
        String label = (String) properties.get("label");
        if (label != null)
            computerData.setLabel(label);
        return computerData;
    }

    private File getComputerDataFile(int computerId) {
        final File computerFolder = new File(_configFolder, String.valueOf(computerId));
        return new File(computerFolder, "data.properties");
    }

    private void saveComputerData(ComputerData computerData) {
        final int id = computerData.getId();
        Properties properties = new Properties();
        properties.setProperty("id", String.valueOf(id));
        if (computerData.getLabel() != null)
            properties.setProperty("label", computerData.getLabel());
        properties.setProperty("owner", computerData.getOwner());
        try {
            Writer writer = new FileWriter(getComputerDataFile(computerData.getId()));
            try {
                properties.store(writer, null);
            } finally {
                writer.close();
            }
        } catch (IOException exp) {
            // TODO
        }
    }

    @ForgeSubscribe
    public void loadComputerData(ChunkEvent.Load event) {
        final Chunk chunk = event.getChunk();
        Collection<TileEntity> entities = chunk.chunkTileEntityMap.values();
        for (TileEntity entity : entities) {
            if (entity instanceof ComputerTileEntity) {
                final int computerId = ((ComputerTileEntity) entity).getComputerId();
                final ComputerData computerData = readComputerDataFromDisk(computerId);
                _computerDataMap.put(computerId, computerData);
            }
        }
    }

    @ForgeSubscribe
    public void unloadComputerData(ChunkEvent.Unload event) {
        final Chunk chunk = event.getChunk();
        Collection<TileEntity> entities = chunk.chunkTileEntityMap.values();
        for (TileEntity entity : entities) {
            if (entity instanceof ComputerTileEntity) {
                final ComputerData computerData = _computerDataMap.remove(((ComputerTileEntity) entity).getComputerId());
                saveComputerData(computerData);
            }
        }
    }
}
