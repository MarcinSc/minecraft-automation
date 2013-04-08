package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.automation.computer.ComputerData;

import java.util.HashMap;
import java.util.Map;

public class ServerAutomationRegistry implements AutomationRegistry {
    private Map<Integer, ComputerData> _computerDataMap = new HashMap<Integer, ComputerData>();
    private int _nextId;

    @Override
    public String getComputerLabel(int computerId) {
        return _computerDataMap.get(computerId).getLabel();
    }

    public int assignNextComputerId() {
        int result = _nextId;
        _nextId++;
        return result;
    }

    @Override
    public ComputerData getComputerData(int computerId) {
        return _computerDataMap.get(computerId);
    }
}
