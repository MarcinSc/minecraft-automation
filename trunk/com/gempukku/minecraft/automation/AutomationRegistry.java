package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.automation.computer.ComputerData;
import com.gempukku.minecraft.automation.computer.ComputerModule;
import net.minecraft.item.Item;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AutomationRegistry {
    private static AutomationRegistry _instance = new AutomationRegistry();

    private Map<Integer, ComputerData> _computerRegistry = new ConcurrentHashMap<Integer, ComputerData>();
    private Map<Item, ComputerModule> _computerModules = new ConcurrentHashMap<Item, ComputerModule>();
    private int _nextInt;

    public void registerComputerModule(Item item, ComputerModule computerModule) {
        _computerModules.put(item, computerModule);
    }

    public ComputerData getComputerData(int id) {
        return _computerRegistry.get(id);
    }
}
