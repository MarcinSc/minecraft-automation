package com.gempukku.minecraft.automation.computer.module.storage;

import com.gempukku.minecraft.automation.AutomationUtils;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.module.ModuleComputerCallback;
import com.gempukku.minecraft.automation.computer.module.ModuleFunctionExecutable;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.world.World;

import java.util.Map;

public class GetSelfSlotCountFunction implements ModuleFunctionExecutable {
    @Override
    public int getDuration() {
        return 100;
    }

    @Override
    public int getMinimumExecutionTicks() {
        return 1;
    }

    @Override
    public String[] getParameterNames() {
        return new String[0];
    }

    @Override
    public Object executeFunction(int line, World world, ModuleComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
        final ComputerTileEntity computerTileEntity = AutomationUtils.getComputerEntitySafely(world, computer);
        if (computerTileEntity == null)
            return 0;
        return computerTileEntity.getItemSlotsCount();
    }
}
