package com.gempukku.minecraft.automation.computer;

import com.gempukku.minecraft.automation.lang.ExecutionContext;

public class ComputerExecutionContext extends ExecutionContext {
    private ComputerData _computerData;

    public ComputerExecutionContext(ComputerData computerData) {
        _computerData = computerData;
    }

    public ComputerData getComputerData() {
        return _computerData;
    }
}
