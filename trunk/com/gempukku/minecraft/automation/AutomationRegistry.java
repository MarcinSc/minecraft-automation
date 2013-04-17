package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.automation.computer.ComputerSpec;
import com.gempukku.minecraft.automation.module.ComputerModule;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.util.Collection;

/**
 * This class is used to control the data associated with Automation module.
 */
public interface AutomationRegistry {
    public String getComputerLabel(String worldName, int computerId);

    public Item getModuleItemByType(String moduleType);

    public int getModuleItemMetadataByType(String moduleType);

    public Collection<Integer> getModuleItemMetadataForItem(int itemId);

    public ComputerModule getModuleByItemId(int itemId, int metadata);

    public void registerComputerModule(Item moduleItem, int metadata, ComputerModule module);

    public void registerComputerSpec(Block computerBlock, ComputerSpec computerSpec);
}
