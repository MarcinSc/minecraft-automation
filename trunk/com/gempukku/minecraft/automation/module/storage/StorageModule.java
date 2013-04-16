package com.gempukku.minecraft.automation.module.storage;

import com.gempukku.minecraft.automation.lang.FunctionExecutable;
import com.gempukku.minecraft.automation.module.AbstractComputerModule;

public class StorageModule extends AbstractComputerModule {
    private FunctionExecutable _hasContainerInFront = new HasContainerFunction(ComputerSide.FRONT);
    private FunctionExecutable _hasContainerOnLeft = new HasContainerFunction(ComputerSide.LEFT);
    private FunctionExecutable _hasContainerOnRight = new HasContainerFunction(ComputerSide.RIGHT);

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
        if (name.equals("hasContainerInFront"))
            return _hasContainerInFront;
        else if (name.equals("hasContainerOnLeft"))
            return _hasContainerOnLeft;
        else if (name.equals("hasContainerOnRight"))
            return _hasContainerOnRight;
        return null;
    }
}
