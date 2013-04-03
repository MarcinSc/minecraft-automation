package com.gempukku.minecraft.automation.computer.bind;

import com.gempukku.minecraft.automation.computer.ComputerData;
import com.gempukku.minecraft.automation.lang.ObjectDefinition;
import com.gempukku.minecraft.automation.lang.Variable;

public class SlotBindingObjectDefinition implements ObjectDefinition {
    private ComputerData _computerData;
    private int _slotNo;

    public SlotBindingObjectDefinition(ComputerData computerData, int slotNo) {
        _computerData = computerData;
        _slotNo = slotNo;
    }

    @Override
    public Variable getMember(String name) {
        return null;
    }
}
