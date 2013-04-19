package com.gempukku.minecraft.automation.client;

import com.gempukku.minecraft.automation.AutomationProxy;

import java.io.File;

public class ClientAutomationProxy implements AutomationProxy {
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
