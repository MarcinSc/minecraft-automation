package com.gempukku.minecraft.automation.server;

import com.gempukku.minecraft.automation.AbstractAutomationRegistry;
import com.gempukku.minecraft.automation.ComputerEvent;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ServerAutomationRegistry extends AbstractAutomationRegistry {
    private Map<Integer, ServerComputerData> _computerDataMap = new HashMap<Integer, ServerComputerData>();
    private File _savesFolder;
    private int _nextId = 1;

    public ServerAutomationRegistry(File savesFolder) {
        _savesFolder = savesFolder;
    }

    @ForgeSubscribe
    public void setComputerCoordinatesAndFacing(ComputerEvent.ComputerAddedToWorldEvent evt) {
        final ComputerTileEntity computerTileEntity = evt.getComputerTileEntity();
        final ServerComputerData computerData = getComputerData(evt.getWorld(), computerTileEntity.getComputerId());
        computerData.setLocation(computerTileEntity.xCoord, computerTileEntity.yCoord, computerTileEntity.zCoord);
        computerData.setFacing(evt.getWorld().getBlockMetadata(computerTileEntity.xCoord, computerTileEntity.yCoord, computerTileEntity.zCoord));
    }

    @ForgeSubscribe
    public void updateComputerCoordinatesAndFacing(ComputerEvent.ComputerMovedInWorldEvent evt) {
        final ComputerTileEntity computerTileEntity = evt.getComputerTileEntity();
        final ServerComputerData computerData = getComputerData(evt.getWorld(), computerTileEntity.getComputerId());
        computerData.setLocation(computerTileEntity.xCoord, computerTileEntity.yCoord, computerTileEntity.zCoord);
        computerData.setFacing(evt.getWorld().getBlockMetadata(computerTileEntity.xCoord, computerTileEntity.yCoord, computerTileEntity.zCoord));
    }

    @Override
    public String getComputerLabel(String worldName, int computerId) {
        return getComputerData(worldName, computerId).getLabel();
    }

    public int getComputerSpeed(String computerType) {
        return getComputerSpecByType(computerType).speed;
    }

    public int getComputerMaxMemory(String computerType) {
        return getComputerSpecByType(computerType).memory;
    }

    public int getComputerMaxStackSize(String computerType) {
        return getComputerSpecByType(computerType).maxStackSize;
    }

    public int storeNewComputer(World world, String owner, String computerType) {
        int computerId = _nextId;
        _nextId++;
        final File computerDataFile = getComputerDataFile(world.getWorldInfo().getWorldName(), computerId);
        computerDataFile.getParentFile().mkdirs();
        ServerComputerData computerData = new ServerComputerData(computerId, owner, computerType);
        _computerDataMap.put(computerId, computerData);
        saveComputerDataToDisk(world, computerId);
        return computerId;
    }

    private ServerComputerData getComputerData(String worldName, int computerId) {
        ServerComputerData computerData = _computerDataMap.get(computerId);
        if (computerData == null) {
            computerData = readComputerDataFromDisk(worldName, computerId);
            if (computerData != null)
                _computerDataMap.put(computerId, computerData);
        }
        return computerData;
    }

    public ServerComputerData getComputerData(World world, int computerId) {
        return getComputerData(world.getWorldInfo().getWorldName(), computerId);
    }

    private ServerComputerData readComputerDataFromDisk(String worldName, int computerId) {
        File computerDataFile = getComputerDataFile(worldName, computerId);

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

        int id = Integer.parseInt(properties.getProperty("id"));
        String owner = properties.getProperty("owner");
        String computerType = properties.getProperty("computerType");
        String label = properties.getProperty("label");

        ServerComputerData computerData = new ServerComputerData(id, owner, computerType);
        if (label != null)
            computerData.setLabel(label);

        return computerData;
    }

    private void saveComputerDataToDisk(World world, int computerId) {
        File computerDataFile = getComputerDataFile(world.getWorldInfo().getWorldName(), computerId);
        computerDataFile.getParentFile().mkdirs();

        final ServerComputerData serverComputerData = _computerDataMap.get(computerId);

        Properties properties = new Properties();
        properties.setProperty("id", String.valueOf(computerId));
        properties.setProperty("owner", serverComputerData.getOwner());
        properties.setProperty("computerType", serverComputerData.getComputerType());
        if (serverComputerData.getLabel() != null)
            properties.setProperty("label", serverComputerData.getLabel());
        try {
            FileWriter writer = new FileWriter(computerDataFile);
            try {
                properties.store(writer, null);
            } finally {
                writer.close();
            }
        } catch (IOException exp) {
            // TODO
        }
    }

    private File getComputerDataFile(String worldName, int computerId) {
        File worldFolder = new File(_savesFolder, worldName);
        File automationFolder = new File(worldFolder, "automation");
        final File computerFolder = new File(automationFolder, String.valueOf(computerId));
        return new File(computerFolder, "data.properties");
    }
}
