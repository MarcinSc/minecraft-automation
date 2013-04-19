package com.gempukku.minecraft.automation.client;

import com.gempukku.minecraft.automation.CommonAutomationProxy;

import java.io.File;

public class ClientAutomationProxy extends CommonAutomationProxy {
	private ClientAutomationRegistry _automationRegistry;

	@Override
	public void initialize(File modConfigDirectory) {
		_automationRegistry = new ClientAutomationRegistry();
	}

	@Override
	public ClientAutomationRegistry getRegistry() {
		return _automationRegistry;
	}
}
