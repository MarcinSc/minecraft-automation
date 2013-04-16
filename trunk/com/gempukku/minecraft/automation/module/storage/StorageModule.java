package com.gempukku.minecraft.automation.module.storage;

import com.gempukku.minecraft.automation.lang.FunctionExecutable;
import com.gempukku.minecraft.automation.module.AbstractComputerModule;

public class StorageModule extends AbstractComputerModule {
    private FunctionExecutable _hasContainer = new HasContainerFunction();
    private FunctionExecutable _getSlotCount = new GetSlotCountFunction();
    private FunctionExecutable _getSelfSlotCount = new GetSelfSlotCountFunction();

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
        return null;
    }
}
