package com.gempukku.minecraft.automation.module.mobility;

import com.gempukku.minecraft.MinecraftUtils;
import com.gempukku.minecraft.automation.Automation;
import com.gempukku.minecraft.automation.AutomationUtils;
import com.gempukku.minecraft.automation.ComputerEvent;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.JavaFunctionExecutable;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.block.material.Material;
import net.minecraft.util.Facing;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.util.Map;

public class MoveForwardFunction extends JavaFunctionExecutable {
    @Override
    protected int getDuration() {
        return 100;
    }

    @Override
    public String[] getParameterNames() {
        return new String[0];
    }

    @Override
    protected Object executeFunction(ServerComputerData computer, World world, Map<String, Variable> parameters) throws ExecutionException {
        final int facing = computer.getFacing();
        final int newX = computer.getX() + Facing.offsetsXForSide[facing];
        final int newY = computer.getY() + Facing.offsetsYForSide[facing];
        final int newZ = computer.getZ() + Facing.offsetsZForSide[facing];

        final Material blockMaterial = world.getBlockMaterial(newX, newY, newZ);
        if (!blockMaterial.isReplaceable())
            return false;

        final ComputerTileEntity tileEntity = AutomationUtils.getComputerEntitySafely(world, computer);
        if (tileEntity == null)
            return false;

        world.setBlockToAir(computer.getX(), computer.getY(), computer.getZ());
        world.removeBlockTileEntity(computer.getX(), computer.getY(), computer.getZ());

        world.setBlockAndMetadataWithNotify(newX, newY, newZ, Automation.computerBlock.blockID, facing, 2);
        MinecraftUtils.updateTileEntity(world, newX, newY, newZ, tileEntity);
        MinecraftForge.EVENT_BUS.post(new ComputerEvent.ComputerMovedInWorldEvent(world, tileEntity));

        return true;
    }
}
