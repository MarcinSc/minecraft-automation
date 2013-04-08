package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.automation.computer.ComputerData;

import java.util.HashMap;
import java.util.Map;

public class ClientAutomationRegistry implements AutomationRegistry {
    private Map<Integer, String> _computerLabels = new HashMap<Integer, String>();
    
    @Override
    public int assignNextComputerId() {
        throw new UnsupportedOperationException("Client cannot assign computer id");
    }

    @Override
    public ComputerData getComputerData(int computerId) {
        throw new UnsupportedOperationException("Client cannot get ComputerData");
    }

    @Override
    public String getComputerLabel(int computerId) {
        String result = _computerLabels.get(computerId);
        if (result == null) {
            // request the server to send the label
        }
        return result;
    }
}
