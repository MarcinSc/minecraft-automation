package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.automation.module.ComputerModule;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class ComputerTileEntity extends TileEntity implements IInventory {
    private static final String ID_NAME = "computerId";
    private static final String FACING = "facing";
    private static final String RUNNING = "running";
    private static final String MODULE_SLOTS_COUNT = "moduleSlots";
    private int _computerId;
    private int _facing;
    private boolean _runningProgram;
    private ComputerModule[] _modules;
    private int _moduleSlotsCount;

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

    public void setRunningProgram(boolean runningProgram) {
        _runningProgram = runningProgram;
    }

    public boolean isRunningProgram() {
        return _runningProgram;
    }

    public int getModuleSlotsCount() {
        return _moduleSlotsCount;
    }

    public void setModuleSlotsCount(int moduleSlots) {
        _moduleSlotsCount = moduleSlots;
    }

    public ComputerModule getModule(int slot) {
        return _modules[slot];
    }

    public void setModule(int slot, ComputerModule module) {
        _modules[slot] = module;
    }

    @Override
    public void openChest() {
    }

    @Override
    public void closeChest() {
    }

    @Override
    public int getSizeInventory() {
        return _moduleSlotsCount;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return createStackFromModuleInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        if (_modules[slot] != null && count > 0) {
            ItemStack stack = createStackFromModuleInSlot(slot);
            _modules[slot] = null;
            onInventoryChanged();
            return stack;
        }
        return null;
    }

    private ItemStack createStackFromModuleInSlot(int slot) {
        return new ItemStack(Automation.proxy.getRegistry().getModuleItemByType(_modules[slot].getModuleType()));
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return createStackFromModuleInSlot(slot);
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        _modules[i] = Automation.proxy.getRegistry().getModuleByItemId(itemstack.itemID);
    }

    @Override
    public String getInvName() {
        return "Computer";
    }

    @Override
    public boolean isInvNameLocalized() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return true;
    }

    @Override
    public boolean isStackValidForSlot(int i, ItemStack itemstack) {
        final ComputerModule module = Automation.proxy.getRegistry().getModuleByItemId(itemstack.itemID);
        return module != null;
    }

    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        _computerId = par1NBTTagCompound.getInteger(ID_NAME);
        _facing = par1NBTTagCompound.getInteger(FACING);
        _runningProgram = par1NBTTagCompound.getBoolean(RUNNING);
        _moduleSlotsCount = par1NBTTagCompound.getInteger(MODULE_SLOTS_COUNT);
        _modules = new ComputerModule[_moduleSlotsCount];
    }

    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        par1NBTTagCompound.setInteger(ID_NAME, _computerId);
        par1NBTTagCompound.setInteger(FACING, _facing);
        par1NBTTagCompound.setBoolean(RUNNING, _runningProgram);
        par1NBTTagCompound.setInteger(MODULE_SLOTS_COUNT, _moduleSlotsCount);
    }
}
