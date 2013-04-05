package com.gempukku.minecraft.automation;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class ComputerTileEntity extends TileEntity {
    private static final String ID_NAME = "computerId";
    private static final String FACING = "facing";
    private int _computerId;
    private int _facing;

    public int getComputerId() {
        return _computerId;
    }

    public void setComputerId(int computerId) {
        _computerId = computerId;
    }

    public int getFacing() {
        return _facing;
    }

    public void setFacing(int facing) {
        _facing = facing;
    }

    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        _computerId = par1NBTTagCompound.getInteger(ID_NAME);
        _facing = par1NBTTagCompound.getInteger(FACING);
    }

    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger(ID_NAME, _computerId);
        par1NBTTagCompound.setInteger(FACING, _facing);
    }
}
