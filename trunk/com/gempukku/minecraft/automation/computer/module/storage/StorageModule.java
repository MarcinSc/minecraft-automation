package com.gempukku.minecraft.automation.computer.module.storage;

import com.gempukku.minecraft.automation.computer.module.AbstractComputerModule;
import com.gempukku.minecraft.automation.computer.module.ModuleFunctionExecutable;

public class StorageModule extends AbstractComputerModule {
	private ModuleFunctionExecutable _hasContainer = new HasContainerFunction();
	private ModuleFunctionExecutable _getSlotCount = new GetSlotCountFunction();
	private ModuleFunctionExecutable _getSelfSlotCount = new GetSelfSlotCountFunction();
	private ModuleFunctionExecutable _getItemCount = new GetItemCountFunction();
	private ModuleFunctionExecutable _getSelfItemCount = new GetSelfItemCountFunction();
	private ModuleFunctionExecutable _transferToSelf = new TransferToSelfFunction();
	private ModuleFunctionExecutable _transferFromSelf = new TransferFromSelfFunction();
	private ModuleFunctionExecutable _createWaitForItemInSelf = new CreateWaitForItemInSelfFunction();

	@Override
	public boolean hasInventoryManipulator() {
		return true;
	}

	@Override
	public int getStorageSlots() {
		return 5;
	}

	@Override
	public String getModuleType() {
		return "Storage";
	}

	@Override
	public String getModuleName() {
		return "Storage module";
	}

	@Override
	public ModuleFunctionExecutable getFunctionByName(String name) {
		if (name.equals("hasContainer"))
			return _hasContainer;
		else if (name.equals("getSlotCount"))
			return _getSlotCount;
		else if (name.equals("getSelfSlotCount"))
			return _getSelfSlotCount;
		else if (name.equals("getItemCount"))
			return _getItemCount;
		else if (name.equals("getSelfItemCount"))
			return _getSelfItemCount;
		else if (name.equals("transferToSelf"))
			return _transferToSelf;
		else if (name.equals("transferFromSelf"))
			return _transferFromSelf;
		else if (name.equals("createWaitForItemInSelf"))
			return _createWaitForItemInSelf;
		return null;
	}
}
