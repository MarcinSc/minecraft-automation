package com.gempukku.minecraft.automation;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

public class AutomationUtils {
    public static ComputerTileEntity getComputerEntitySafely(IBlockAccess blockAccess, int x, int y, int z) {
        final TileEntity tileEntity = blockAccess.getBlockTileEntity(x, y, z);
        if (tileEntity != null && tileEntity instanceof ComputerTileEntity)
            return (ComputerTileEntity) tileEntity;
        return null;
    }
}
