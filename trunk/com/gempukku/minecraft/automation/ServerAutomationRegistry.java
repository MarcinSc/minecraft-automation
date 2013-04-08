package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.automation.computer.ComputerData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.ChunkEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ServerAutomationRegistry implements AutomationRegistry {
    private Map<Integer, ComputerData> _computerDataMap = new HashMap<Integer, ComputerData>();
    private int _nextId;

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
        ComputerData computerData = _computerDataMap.get(computerId);
        if (computerData == null) {
            computerData = readComputerDataFromDisk(computerId);
            _computerDataMap.put(computerId, computerData);
        }
        return computerData;
    }

    private ComputerData readComputerDataFromDisk(int computerId) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    @ForgeSubscribe
    public void unloadComputerData(ChunkEvent.Unload event) {
        final Chunk chunk = event.getChunk();
        Collection<TileEntity> entities = chunk.chunkTileEntityMap.values();
        for (TileEntity entity : entities) {
            if (entity instanceof ComputerTileEntity)
                _computerDataMap.remove(((ComputerTileEntity) entity).getComputerId());
        }
    }
}
