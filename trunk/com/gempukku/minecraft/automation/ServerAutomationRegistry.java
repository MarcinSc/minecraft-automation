package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.automation.computer.ServerComputerData;
import net.minecraftforge.event.ForgeSubscribe;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ServerAutomationRegistry extends AbstractAutomationRegistry {
    private Map<Integer, ServerComputerData> _computerDataMap = new HashMap<Integer, ServerComputerData>();
    private File _configFolder;
    private int _nextId;

    public ServerAutomationRegistry(File configFolder) {
        _configFolder = configFolder;
    }

    @ForgeSubscribe
    public void setComputerCoordinatesAndFacing(ComputerEvent.ComputerAddedToWorldEvent evt) {
        final ComputerTileEntity computerTileEntity = evt.getComputerTileEntity();
        final ServerComputerData computerData = getComputerData(computerTileEntity.getComputerId());
        computerData.setLocation(computerTileEntity.xCoord, computerTileEntity.yCoord, computerTileEntity.zCoord);
        computerData.setFacing(computerTileEntity.getFacing());
    }

    @ForgeSubscribe
    public void updateComputerCoordinatesAndFacing(ComputerEvent.ComputerMovedInWorldEvent evt) {
        final ComputerTileEntity computerTileEntity = evt.getComputerTileEntity();
        final ServerComputerData computerData = getComputerData(computerTileEntity.getComputerId());
        computerData.setLocation(computerTileEntity.xCoord, computerTileEntity.yCoord, computerTileEntity.zCoord);
        computerData.setFacing(computerTileEntity.getFacing());
    }

    @Override
    public String getComputerLabel(int computerId) {
        return getComputerData(computerId).getLabel();
    }

    public int storeNewComputer(String owner) {
        int computerId = _nextId;
        _nextId++;
        final File computerDataFile = getComputerDataFile(computerId);
        computerDataFile.getParentFile().mkdirs();
        ServerComputerData computerData = new ServerComputerData(computerId, owner);
        _computerDataMap.put(computerId, computerData);
        return computerId;
    }

    public ServerComputerData getComputerData(int computerId) {
        final ServerComputerData computerData = _computerDataMap.get(computerId);
        if (computerData == null)
            _computerDataMap.put(computerId, readComputerDataFromDisk(computerId));
        return computerData;
    }

    private ServerComputerData readComputerDataFromDisk(int computerId) {
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
        ServerComputerData computerData = new ServerComputerData(id, owner);
        String label = (String) properties.get("label");
        if (label != null)
            computerData.setLabel(label);
        return computerData;
    }

    private File getComputerDataFile(int computerId) {
        final File computerFolder = new File(_configFolder, String.valueOf(computerId));
        return new File(computerFolder, "data.properties");
    }
}
