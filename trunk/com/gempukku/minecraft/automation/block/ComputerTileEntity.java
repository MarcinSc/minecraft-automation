package com.gempukku.minecraft.automation.block;

import com.gempukku.minecraft.automation.Automation;
import com.gempukku.minecraft.automation.computer.module.ComputerModule;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;

import java.util.*;

public class ComputerTileEntity extends TileEntity implements IInventory {
	private static final String ID_NAME = "computerId";
	private static final String FACING = "facing";
	private static final String STATE = "state";
	public static final short STATE_IDLE = 0;
	public static final short STATE_RUNNING = 1;
	public static final short STATE_SUSPENDED = 2;
	private static final String MODULE_SLOTS_COUNT = "moduleSlots";
	private int _computerId;
	private short _state;

	private ComputerModule[] _modules;
	private ItemStack[] _inventory = new ItemStack[0];

	private Map<Integer, Map<String, String>> _moduleData = new HashMap<Integer, Map<String, String>>();

	private int _moduleSlotsCount;
	private int _facing;

	public IInventory getModuleInventory() {
		return new ModuleInventory();
	}

	private class ModuleInventory implements IInventory {
		@Override
		public void closeChest() {
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

		@Override
		public int getInventoryStackLimit() {
			return 1;
		}

		@Override
		public String getInvName() {
			return "Computer modules";	//To change body of implemented methods use File | Settings | File Templates.
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
		public ItemStack getStackInSlotOnClosing(int slot) {
			return createStackFromModuleInSlot(slot);
		}

		@Override
		public boolean isInvNameLocalized() {
			return false;
		}

		@Override
		public boolean isStackValidForSlot(int i, ItemStack itemstack) {
			final ComputerModule module = Automation.proxy.getRegistry().getModuleByItemId(itemstack.itemID, itemstack.getItemDamage());
			return module != null;
		}

		@Override
		public boolean isUseableByPlayer(EntityPlayer entityplayer) {
			return true;
		}

		@Override
		public void onInventoryChanged() {
			ComputerTileEntity.this.onInventoryChanged();
		}

		@Override
		public void openChest() {
		}

		@Override
		public void setInventorySlotContents(int i, ItemStack itemstack) {
			if (itemstack != null)
				_modules[i] = Automation.proxy.getRegistry().getModuleByItemId(itemstack.itemID, itemstack.getItemDamage());
			else
				_modules[i] = null;
		}
	}

	public Map<String, String> getModuleData(int slotNo) {
		return _moduleData.get(slotNo);
	}

	public void setModuleData(int slotNo, Map<String, String> moduleData) {
		_moduleData.put(slotNo, moduleData);
	}

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

	public short getState() {
		return _state;
	}

	public void setState(short state) {
		_state = state;
	}

	public int getModuleSlotsCount() {
		return _moduleSlotsCount;
	}

	public void setModuleSlotsCount(int moduleSlots) {
		_moduleSlotsCount = moduleSlots;
		_modules = new ComputerModule[_moduleSlotsCount];
	}

	public List<ItemStack> updateItemsSlotCount() {
		List<ItemStack> itemsStacksToPopIntoWorld = new LinkedList<ItemStack>();
		int newSize = getItemSlotsCount();
		if (newSize != _inventory.length) {
			ItemStack[] oldItems = _inventory;
			_inventory = new ItemStack[newSize];
			if (oldItems.length > _inventory.length) {
				for (int i = _inventory.length; i < oldItems.length; i++)
					if (oldItems[i] != null)
						itemsStacksToPopIntoWorld.add(oldItems[i]);
			}
			System.arraycopy(oldItems, 0, _inventory, 0, Math.min(_inventory.length, oldItems.length));
		}
		return itemsStacksToPopIntoWorld;
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

	@Override
	public void openChest() {
	}

	@Override
	public void closeChest() {
	}

	@Override
	public int getSizeInventory() {
		return _inventory.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return _inventory[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int count) {
		if (_inventory[slot] != null && count > 0) {
			if (_inventory[slot].stackSize <= count) {
				ItemStack result = _inventory[slot];
				_inventory[slot] = null;
				onInventoryChanged();
				return result;
			} else {
				ItemStack result = _inventory[slot].splitStack(count);
				onInventoryChanged();
				return result;
			}
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
		return _inventory[slot];
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack) {
		_inventory[slot] = itemStack;
		onInventoryChanged();
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
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public boolean isStackValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		this.writeToNBT(nbttagcompound, true);
		return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, nbttagcompound);
	}

	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
		readFromNBT(pkt.customParam1);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {
		super.readFromNBT(tagCompound);
		_computerId = tagCompound.getInteger(ID_NAME);
		_facing = tagCompound.getInteger(FACING);
		_state = tagCompound.getShort(STATE);
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

		final NBTTagList moduleDataList = tagCompound.getTagList("ModuleData");
		if (moduleDataList != null) {
			for (int i = 0; i < moduleDataList.tagCount(); i++) {
				final NBTTagCompound moduleData = (NBTTagCompound) modules.tagAt(i);
				final Collection tags = moduleData.getTags();
				if (tags.size() > 0) {
					Map<String, String> moduleDataMap = new HashMap<String, String>();
					for (NBTBase nbtBase : (Collection<NBTBase>) tags) {
						final String name = nbtBase.getName();
						moduleDataMap.put(name, moduleData.getString(name));
					}

					_moduleData.put(i, moduleDataMap);
				}
			}
		}

		int itemSlotCount = getItemSlotsCount();
		_inventory = new ItemStack[itemSlotCount];
		NBTTagList items = tagCompound.getTagList("Items");
		for (int i = 0; i < items.tagCount(); i++) {
			NBTTagCompound itemData = (NBTTagCompound) items.tagAt(0);
			int j = itemData.getByte("Slot") & 255;

			if (j >= 0 && j < itemSlotCount)
				_inventory[j] = ItemStack.loadItemStackFromNBT(itemData);
		}
	}

	private void writeToNBT(NBTTagCompound tagCompound, boolean excludeServerData) {
		super.writeToNBT(tagCompound);
		tagCompound.setInteger(ID_NAME, _computerId);
		tagCompound.setInteger(FACING, _facing);
		tagCompound.setShort(STATE, _state);
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

		if (!excludeServerData) {
			NBTTagList moduleDataList = new NBTTagList();
			for (int i = 0; i < _modules.length; ++i) {
				if (_modules[i] != null) {
					NBTTagCompound moduleData = new NBTTagCompound();
					final Map<String, String> moduleDataMap = _moduleData.get(i);
					if (moduleDataMap != null) {
						for (Map.Entry<String, String> entry : moduleDataMap.entrySet())
							moduleData.setString(entry.getKey(), entry.getValue());
					}
					moduleDataList.appendTag(moduleData);
				}
			}
			tagCompound.setTag("ModuleData", moduleDataList);
		}

		NBTTagList itemList = new NBTTagList();
		for (int i = 0; i < _inventory.length; ++i) {
			if (_inventory[i] != null) {
				NBTTagCompound itemData = new NBTTagCompound();
				itemData.setByte("Slot", (byte) i);
				_inventory[i].writeToNBT(itemData);
				itemList.appendTag(itemData);
			}
		}
		tagCompound.setTag("Items", itemList);
	}

	@Override
	public void writeToNBT(NBTTagCompound tagCompound) {
		writeToNBT(tagCompound, false);
	}
}
