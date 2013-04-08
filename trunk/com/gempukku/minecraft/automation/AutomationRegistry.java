package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.automation.computer.ComputerData;

public interface AutomationRegistry {
    public String getComputerLabel(int computerId);
    
    public ComputerData getComputerData(int computerId);

    public int assignNextComputerId();
}
