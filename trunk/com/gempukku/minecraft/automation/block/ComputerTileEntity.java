package com.gempukku.minecraft.automation.block;

import com.gempukku.minecraft.automation.Automation;
import com.gempukku.minecraft.automation.module.ComputerModule;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;

public class ComputerTileEntity extends TileEntity implements IInventory {
    private static final String ID_NAME = "computerId";
    private static final String FACING = "facing";
    private static final String RUNNING = "running";
    private static final String MODULE_SLOTS_COUNT = "moduleSlots";
    private int _computerId;
    private boolean _runningProgram;
    private ComputerModule[] _modules;
    private int _moduleSlotsCount;

    public int getComputerId() {
        return _computerId;
    }

    public void setComputerId(int computerId) {
        _computerId = computerId;
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
        _modules = new ComputerModule[_moduleSlotsCount];
    }

    public int getItemSlotsCount() {
        int count = 0;
        for (ComputerModule module : _modules)
            if (module != null)
                count += module.getStorageSlots();

        return count;
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
        if (_modules[slot] == null)
            return null;
        final String moduleType = _modules[slot].getModuleType();
        return new ItemStack(Automation.proxy.getRegistry().getModuleItemByType(moduleType), 1,
                Automation.proxy.getRegistry().getModuleItemMetadataByType(moduleType));
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return createStackFromModuleInSlot(slot);
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) {
        if (itemstack != null)
            _modules[i] = Automation.proxy.getRegistry().getModuleByItemId(itemstack.itemID, itemstack.getItemDamage());
        else
            _modules[i] = null;
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
        final ComputerModule module = Automation.proxy.getRegistry().getModuleByItemId(itemstack.itemID, itemstack.getItemDamage());
        return module != null;
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.writeToNBT(nbttagcompound);
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, nbttagcompound);
    }

    @Override
    public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
        readFromNBT(pkt.customParam1);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        _computerId = tagCompound.getInteger(ID_NAME);
        _runningProgram = tagCompound.getBoolean(RUNNING);
        _moduleSlotsCount = tagCompound.getInteger(MODULE_SLOTS_COUNT);
        _modules = new ComputerModule[_moduleSlotsCount];

        final NBTTagList modules = tagCompound.getTagList("Modules");
        for (int i = 0; i < modules.tagCount(); i++) {
            final NBTTagCompound moduleData = (NBTTagCompound) modules.tagAt(i);
            int slot = moduleData.getByte("Slot");
            int id = moduleData.getInteger("Id");
            int metadata = moduleData.getInteger("MD");
            _modules[slot] = Automation.proxy.getRegistry().getModuleByItemId(id, metadata);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setInteger(ID_NAME, _computerId);
        tagCompound.setBoolean(RUNNING, _runningProgram);
        tagCompound.setInteger(MODULE_SLOTS_COUNT, _moduleSlotsCount);

        NBTTagList moduleList = new NBTTagList();
        for (int i = 0; i < _modules.length; ++i) {
            if (_modules[i] != null) {
                NBTTagCompound moduleData = new NBTTagCompound();
                moduleData.setByte("Slot", (byte) i);
                final String moduleType = _modules[i].getModuleType();
                moduleData.setInteger("Id", Automation.proxy.getRegistry().getModuleItemByType(moduleType).itemID);
                moduleData.setInteger("MD", Automation.proxy.getRegistry().getModuleItemMetadataByType(moduleType));
                moduleList.appendTag(moduleData);
            }
        }
        tagCompound.setTag("Modules", moduleList);
    }
}
