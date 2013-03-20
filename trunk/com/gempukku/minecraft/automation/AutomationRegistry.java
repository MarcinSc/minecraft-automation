package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.automation.computer.ComputerData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AutomationRegistry {
    private static Map<Integer, ComputerData> _computerRegistry = new ConcurrentHashMap<Integer, ComputerData>();
    private static int _nextInt;

    public static ComputerData getComputerData(int id) {
        return _computerRegistry.get(id);
    }
}
