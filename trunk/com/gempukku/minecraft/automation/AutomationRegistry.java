package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.automation.module.ComputerModule;
import net.minecraft.item.Item;

public interface AutomationRegistry {
    public String getComputerLabel(int computerId);

    public Item getModuleItemByType(String moduleType);

    public ComputerModule getModuleByItemId(int itemID);

    public void registerComputerModule(Item moduleItem, ComputerModule module);
}
