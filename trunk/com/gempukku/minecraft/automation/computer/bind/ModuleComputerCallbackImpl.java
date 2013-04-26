package com.gempukku.minecraft.automation.computer.bind;

import com.gempukku.minecraft.MinecraftUtils;
import com.gempukku.minecraft.automation.AutomationUtils;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.ComputerCallback;
import com.gempukku.minecraft.automation.computer.ComputerConsole;
import com.gempukku.minecraft.automation.computer.module.ModuleComputerCallback;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

import java.util.Map;

public class ModuleComputerCallbackImpl implements ModuleComputerCallback {
	private World _world;
	private int _slotNo;
	private ComputerCallback _computerCallback;

	public ModuleComputerCallbackImpl(World world, int slotNo, ComputerCallback computerCallback) {
		_world = world;
		_slotNo = slotNo;
		_computerCallback = computerCallback;
	}

	@Override
	public ChunkPosition getChunkPosition() {
		return _computerCallback.getChunkPosition();
	}

	@Override
	public ComputerConsole getConsole() {
		return _computerCallback.getConsole();
	}

	@Override
	public int getFacing() {
		return _computerCallback.getFacing();
	}

	@Override
	public int getId() {
		return _computerCallback.getId();
	}

	@Override
	public String getOwner() {
		return _computerCallback.getOwner();
	}

	@Override
	public Map<String, String> getModuleData() {
		final ComputerTileEntity computerEntity = AutomationUtils.getComputerEntitySafely(_world, _computerCallback);
		return computerEntity.getModuleData(_slotNo);
	}

	@Override
	public void setModuleData(Map<String, String> moduleData, boolean notifyClient) {
		final ComputerTileEntity computerEntity = AutomationUtils.getComputerEntitySafely(_world, _computerCallback);
		computerEntity.setModuleData(_slotNo, moduleData);
		if (notifyClient)
			MinecraftUtils.sendTileEntityUpdateToPlayers(_world, computerEntity);
	}
}
