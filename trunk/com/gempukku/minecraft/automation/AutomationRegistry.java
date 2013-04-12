package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.automation.module.ComputerModule;
import net.minecraft.item.Item;

/**
 * This class is used to control the data associated with Automation module.
 */
public interface AutomationRegistry {
    public String getComputerLabel(int computerId);

    public Item getModuleItemByType(String moduleType);

    public ComputerModule getModuleByItemId(int itemID);

    public void registerComputerModule(Item moduleItem, ComputerModule module);
}
