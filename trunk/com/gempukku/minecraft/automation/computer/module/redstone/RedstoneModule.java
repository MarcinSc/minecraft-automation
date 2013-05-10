package com.gempukku.minecraft.automation.computer.module.redstone;

import com.gempukku.minecraft.automation.computer.module.AbstractComputerModule;
import com.gempukku.minecraft.automation.computer.module.ModuleComputerCallback;
import com.gempukku.minecraft.automation.computer.module.ModuleFunctionExecutable;
import net.minecraft.world.IBlockAccess;

import java.util.Map;

public class RedstoneModule extends AbstractComputerModule {
    private ModuleFunctionExecutable _emitStrongSignal = new EmitStrongSignalFunction();
    private ModuleFunctionExecutable _emitWeakSignal = new EmitWeakSignalFunction();
    private ModuleFunctionExecutable _getStrongSignal = new GetStrongSignalFunction();
    private ModuleFunctionExecutable _getWeakSignal = new GetWeakSignalFunction();

    @Override
    public String getModuleType() {
        return "Redstone";
    }

    @Override
    public String getModuleName() {
        return "Redstone module";
    }

    @Override
    public ModuleFunctionExecutable getFunctionByName(String name) {
        if (name.equals("emitStrongSignal"))
            return _emitStrongSignal;
        else if (name.equals("emitWeakSignal"))
            return _emitWeakSignal;
        else if (name.equals("getStrongSignal"))
            return _getStrongSignal;
        else if (name.equals("getWeakSignal"))
            return _getWeakSignal;
        return null;
    }

    @Override
    public int getStrongRedstoneSignalStrengthOnSide(ModuleComputerCallback computerCallback, int input, IBlockAccess blockAccess, int side) {
        Map<String, String> moduleData = computerCallback.getModuleData();
        String strengthValue = moduleData.get("strong-" + side);
        if (strengthValue != null)
            return Math.max(input, Integer.parseInt(strengthValue));
        return input;
    }

    @Override
    public int getWeakRedstoneSignalStrengthOnSide(ModuleComputerCallback computerCallback, int input, IBlockAccess blockAccess, int side) {
        Map<String, String> moduleData = computerCallback.getModuleData();
        String strengthValue = moduleData.get("weak-" + side);
        if (strengthValue != null)
            return Math.max(input, Integer.parseInt(strengthValue));
        return input;
    }
}
