package com.gempukku.minecraft.automation.computer;

import com.gempukku.minecraft.automation.Automation;
import net.minecraft.world.ChunkPosition;

public class ServerComputerData implements ComputerCallback {
	private ComputerConsole _console = new ComputerConsole();
	private String _label;
	private String _computerType;
	private ChunkPosition _chunkPosition;
	private int _facing;
	private int _id;
	private int _dimension;
	private String _owner;

	public ServerComputerData(int id, int dimension, int x, int y, int z, int facing, String owner, String computerType) {
		_id = id;
		_dimension = dimension;
		_chunkPosition = new ChunkPosition(x, y, z);
		_facing = facing;
		_owner = owner;
		_computerType = computerType;
	}

	public ComputerConsole getConsole() {
		return _console;
	}

	public int getId() {
		return _id;
	}

	public int getDimension() {
		return _dimension;
	}

	public String getLabel() {
		return _label;
	}

	public void setLabel(String label) {
		_label = label;
	}

	public String getOwner() {
		return _owner;
	}

	public String getComputerType() {
		return _computerType;
	}

	public int getSpeed() {
		return Automation.proxy.getRegistry().getComputerSpecByType(_computerType).speed;
	}

	public int getMaxStackSize() {
		return Automation.proxy.getRegistry().getComputerSpecByType(_computerType).maxStackSize;
	}

	public int getMaxMemory() {
		return Automation.proxy.getRegistry().getComputerSpecByType(_computerType).memory;
	}

	public void appendToConsole(String text) {
		_console.appendString(text);
	}

	public void setLocation(int x, int y, int z) {
		_chunkPosition = new ChunkPosition(x, y, z);
	}

	public void setFacing(int facing) {
		_facing = facing;
	}

	public int getFacing() {
		return _facing;
	}

	public ChunkPosition getChunkPosition() {
		return _chunkPosition;
	}
}
