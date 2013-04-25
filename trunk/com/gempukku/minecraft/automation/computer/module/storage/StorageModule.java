package com.gempukku.minecraft.automation.computer.module.storage;

import com.gempukku.minecraft.automation.computer.module.AbstractComputerModule;
import com.gempukku.minecraft.automation.lang.FunctionExecutable;

public class StorageModule extends AbstractComputerModule {
	private FunctionExecutable _hasContainer = new HasContainerFunction();
	private FunctionExecutable _getSlotCount = new GetSlotCountFunction();
	private FunctionExecutable _getSelfSlotCount = new GetSelfSlotCountFunction();
	private FunctionExecutable _getItemCount = new GetItemCountFunction();
	private FunctionExecutable _getSelfItemCount = new GetSelfItemCountFunction();
	private FunctionExecutable _transferToSelf = new TransferToSelfFunction();
	private FunctionExecutable _transferFromSelf = new TransferFromSelfFunction();
	private FunctionExecutable _createWaitForItemInSelf = new CreateWaitForItemInSelfFunction();

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
	public FunctionExecutable getFunctionByName(String name) {
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
