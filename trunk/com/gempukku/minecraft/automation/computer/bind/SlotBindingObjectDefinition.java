package com.gempukku.minecraft.automation.computer.bind;

import com.gempukku.minecraft.automation.lang.ObjectDefinition;
import com.gempukku.minecraft.automation.lang.Variable;

public class SlotBindingObjectDefinition implements ObjectDefinition {
    private int _slotNo;

    public SlotBindingObjectDefinition(int slotNo) {
        _slotNo = slotNo;
    }

    @Override
    public Variable getMember(String name) {
        return null;
    }
}
