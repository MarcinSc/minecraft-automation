package com.gempukku.minecraft.automation;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class ComputerTileEntity extends TileEntity {
    private static final String ID_NAME = "id";
    private int _computerId;

    public int getComputerId() {
        return _computerId;
    }

    public void setComputerId(int computerId) {
        _computerId = computerId;
    }

    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        _computerId = par1NBTTagCompound.getInteger(ID_NAME);
    }

    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger(ID_NAME, _computerId);
    }
}
