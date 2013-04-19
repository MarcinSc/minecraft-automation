package com.gempukku.minecraft.automation.server;

import com.gempukku.minecraft.automation.CommonAutomationProxy;
import com.gempukku.minecraft.automation.program.ComputerProcessing;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;

public class ServerAutomationProxy extends CommonAutomationProxy {
	private ServerAutomationRegistry _automationRegistry;
	private ComputerProcessing _programProcessing;

	@Override
	public void initialize(File modConfigDirectory) {
		File savesFolder = new File(modConfigDirectory.getParentFile(), "saves");
		_automationRegistry = new ServerAutomationRegistry(savesFolder);
		_programProcessing = new ComputerProcessing(savesFolder, _automationRegistry);
		MinecraftForge.EVENT_BUS.register(_programProcessing);
	}

	public ComputerProcessing getComputerProcessing() {
		return _programProcessing;
	}

	@Override
	public ServerAutomationRegistry getRegistry() {
		return _automationRegistry;
	}
}
