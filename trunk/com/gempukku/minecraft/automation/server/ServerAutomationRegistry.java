package com.gempukku.minecraft.automation.server;

import com.gempukku.minecraft.automation.AbstractAutomationRegistry;
import com.gempukku.minecraft.automation.AutomationUtils;
import com.gempukku.minecraft.automation.ComputerEvent;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

public class ServerAutomationRegistry extends AbstractAutomationRegistry {
	private Map<Integer, ServerComputerData> _computerDataMap = new HashMap<Integer, ServerComputerData>();
	private File _savesFolder;
	private int _nextId = 1;

	public ServerAutomationRegistry(File savesFolder) {
		_savesFolder = savesFolder;
	}

	public void ensureComputerLoaded(World world, ComputerTileEntity computerTileEntity) {
		final int computerId = computerTileEntity.getComputerId();
		if (!_computerDataMap.containsKey(computerId)) {
			final ServerComputerData computerData = readComputerDataFromDisk(computerTileEntity.worldObj.getWorldInfo().getDimension(),
							computerTileEntity.xCoord, computerTileEntity.yCoord, computerTileEntity.zCoord, computerTileEntity.getFacing(), computerId);
			_computerDataMap.put(computerId, computerData);
			MinecraftForge.EVENT_BUS.post(new ComputerEvent.ComputerAddedToWorldEvent(world, computerTileEntity));
			FMLLog.log("Automation", Level.FINE, "Added to world computer with id %d", computerId);
		} else {
			FMLLog.log("Automation", Level.WARNING, "Asked to load computer with id %d, but already had it", computerId);
		}
	}

	public void updateComputerDataCoordinatesAndFacing(ComputerTileEntity computerTileEntity) {
		int computerId = computerTileEntity.getComputerId();
		final ServerComputerData serverComputerData = _computerDataMap.get(computerId);
		serverComputerData.setLocation(computerTileEntity.xCoord, computerTileEntity.yCoord, computerTileEntity.zCoord);
		serverComputerData.setFacing(computerTileEntity.getFacing());
		MinecraftForge.EVENT_BUS.post(new ComputerEvent.ComputerMovedInWorldEvent(computerTileEntity.worldObj, computerTileEntity));
	}

	public void unloadComputer(ComputerTileEntity computerTileEntity) {
		int computerId = computerTileEntity.getComputerId();
		if (_computerDataMap.containsKey(computerId)) {
			FMLLog.log("Automation", Level.FINE, "Removing from world computer with id %d", computerId);
			MinecraftForge.EVENT_BUS.post(new ComputerEvent.ComputerRemovedFromWorldEvent(computerTileEntity.worldObj, computerTileEntity));
			_computerDataMap.remove(computerId);
		} else {
			FMLLog.log("Automation", Level.WARNING, "Asked to unload computer with id %d, but it wasn't there", computerId);
		}
	}

	public void unloadComputersFromDimension(int dimension) {
		final Iterator<ServerComputerData> computerIterator = _computerDataMap.values().iterator();
		while (computerIterator.hasNext()) {
			final ServerComputerData computer = computerIterator.next();
			if (computer.getDimension() == dimension) {
				FMLLog.log("Automation", Level.FINE, "Removing from world computer with id %d due to World unload", computer.getId());
				computerIterator.remove();
			}
		}
	}

	@Override
	public String getComputerLabel(int computerId) {
		return readLabelFromDisk(computerId);
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
