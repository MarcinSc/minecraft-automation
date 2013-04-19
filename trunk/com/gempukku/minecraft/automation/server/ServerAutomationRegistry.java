package com.gempukku.minecraft.automation.server;

import com.gempukku.minecraft.automation.AbstractAutomationRegistry;
import com.gempukku.minecraft.automation.AutomationUtils;
import com.gempukku.minecraft.automation.ComputerEvent;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
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
	public void readServerComputerDataAndStoreInMemory(ComputerEvent.ComputerAddedToWorldEvent evt) {
		final ComputerTileEntity computerTileEntity = evt.getComputerTileEntity();
		final ServerComputerData computerData = readComputerDataFromDisk(
						evt.getWorld().getWorldInfo().getDimension(),
						computerTileEntity.xCoord, computerTileEntity.yCoord, computerTileEntity.zCoord,
						computerTileEntity.getFacing(),
						computerTileEntity.getComputerId());
	}

	@ForgeSubscribe
	public void updateComputerCoordinatesAndFacing(ComputerEvent.ComputerMovedInWorldEvent evt) {
		final ComputerTileEntity computerTileEntity = evt.getComputerTileEntity();
		final ServerComputerData computerData = _computerDataMap.get(computerTileEntity.getComputerId());
		computerData.setLocation(computerTileEntity.xCoord, computerTileEntity.yCoord, computerTileEntity.zCoord);
		computerData.setFacing(computerTileEntity.getFacing());
	}

	@ForgeSubscribe
	public void removeComputerFromMemory(ComputerEvent.ComputerRemovedFromWorldEvent evt) {
		final ComputerTileEntity computerTileEntity = evt.getComputerTileEntity();
		_computerDataMap.remove(computerTileEntity.getComputerId());
	}

	@Override
	public String getComputerLabel(int computerId) {
		return readLabelFromDisk(computerId);
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

	public int storeNewComputerData(String owner, String computerType) {
		int computerId = _nextId;
		_nextId++;
		saveComputerDataToDisk(computerId, owner, computerType, null);
		return computerId;
	}

	public ServerComputerData getComputerData(int computerId) {
		return _computerDataMap.get(computerId);
	}

	private ServerComputerData readComputerDataFromDisk(int dimension, int x, int y, int z, int facing, int computerId) {
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

		String owner = properties.getProperty("owner");
		String computerType = properties.getProperty("computerType");
		String label = properties.getProperty("label");

		ServerComputerData computerData = new ServerComputerData(computerId, dimension, x, y, z, facing, owner, computerType);
		if (label != null)
			computerData.setLabel(label);

		return computerData;
	}

	private String readLabelFromDisk(int computerId) {
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

		return properties.getProperty("label");
	}

	private void saveComputerDataToDisk(ServerComputerData serverComputerData) {
		saveComputerDataToDisk(serverComputerData.getId(), serverComputerData.getOwner(), serverComputerData.getComputerType(), serverComputerData.getLabel());
	}

	private void saveComputerDataToDisk(int computerId, String owner, String computerType, String label) {
		File computerDataFile = getComputerDataFile(computerId);
		computerDataFile.getParentFile().mkdirs();

		Properties properties = new Properties();
		properties.setProperty("owner", owner);
		properties.setProperty("computerType", computerType);
		if (label != null)
			properties.setProperty("label", label);
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

	private File getComputerDataFile(int computerId) {
		final File computerFolder = AutomationUtils.getComputerSavesFolder(_savesFolder, computerId);
		return new File(computerFolder, "data.properties");
	}
}
