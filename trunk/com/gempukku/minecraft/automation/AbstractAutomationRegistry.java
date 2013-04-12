package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.automation.module.ComputerModule;
import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractAutomationRegistry implements AutomationRegistry {
    private Map<Integer, ComputerModule> _modulesByItemId = new HashMap<Integer, ComputerModule>();
    private Map<String, Item> _moduleItemsByType = new HashMap<String, Item>();

    @Override
    public ComputerModule getModuleByItemId(int itemID) {
        return _modulesByItemId.get(itemID);
    }

    @Override
    public Item getModuleItemByType(String moduleType) {
        return _moduleItemsByType.get(moduleType);
    }

    @Override
    public void registerComputerModule(Item moduleItem, ComputerModule module) {
        _modulesByItemId.put(moduleItem.itemID, module);
        _moduleItemsByType.put(module.getModuleType(), moduleItem);
    }
}
