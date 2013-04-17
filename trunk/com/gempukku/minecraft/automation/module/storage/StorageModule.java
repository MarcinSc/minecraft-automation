package com.gempukku.minecraft.automation.module.storage;

import com.gempukku.minecraft.automation.lang.FunctionExecutable;
import com.gempukku.minecraft.automation.module.AbstractComputerModule;

public class StorageModule extends AbstractComputerModule {
    private FunctionExecutable _hasContainer = new HasContainerFunction();
    private FunctionExecutable _getSlotCount = new GetSlotCountFunction();
    private FunctionExecutable _getSelfSlotCount = new GetSelfSlotCountFunction();
    private FunctionExecutable _getItemCount = new GetItemCountFunction();
    private FunctionExecutable _getSelfItemCount = new GetSelfItemCountFunction();
    private FunctionExecutable _transferToSelf = new TransferToSelfFunction();
    private FunctionExecutable _transferFromSelf = new TransferFromSelfFunction();

    @Override
    public boolean hasInventoryManipulator() {
        return true;
    }

    @Override
    public int getStorageSlots() {
        return 9;
    }

    @Override
    public String getModuleType() {
        return "Storage";
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
        return null;
    }
}
