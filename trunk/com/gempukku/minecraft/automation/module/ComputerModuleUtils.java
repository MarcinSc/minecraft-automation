package com.gempukku.minecraft.automation.module;

import com.gempukku.minecraft.automation.AutomationUtils;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import net.minecraft.world.World;

public class ComputerModuleUtils {
    public static boolean canManipulateInventories(World world, ServerComputerData computerData) {
        final ComputerTileEntity computerTileEntity = AutomationUtils.getComputerEntitySafely(world, computerData);
        if (computerTileEntity == null)
            return false;

        final int moduleCount = computerTileEntity.getModuleSlotsCount();
        for (int i = 0; i < moduleCount; i++) {
            final ComputerModule module = computerTileEntity.getModule(i);
            if (module != null && module.hasInventoryManipulator())
                return true;
        }
        return false;
    }
}
