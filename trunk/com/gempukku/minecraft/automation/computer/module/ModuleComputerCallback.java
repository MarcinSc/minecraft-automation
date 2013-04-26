package com.gempukku.minecraft.automation.computer.module;

import com.gempukku.minecraft.automation.computer.ComputerCallback;

import java.util.Map;

public interface ModuleComputerCallback extends ComputerCallback {
	public Map<String, String> getModuleData();

	public void setModuleData(Map<String, String> moduleData);
}
