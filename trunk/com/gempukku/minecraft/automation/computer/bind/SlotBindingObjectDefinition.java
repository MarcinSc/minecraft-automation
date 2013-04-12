package com.gempukku.minecraft.automation.computer.bind;

import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.FunctionExecutable;
import com.gempukku.minecraft.automation.lang.ObjectDefinition;
import com.gempukku.minecraft.automation.lang.Variable;
import com.gempukku.minecraft.automation.module.ComputerModule;

public class SlotBindingObjectDefinition implements ObjectDefinition {
    private ServerComputerData _computerData;
    private int _slotNo;

    public SlotBindingObjectDefinition(ServerComputerData computerData, int slotNo) {
        _computerData = computerData;
        _slotNo = slotNo;
    }

    @Override
    public Variable getMember(String name) {
        final ComputerModule moduleAt = _computerData.getModuleAt(_slotNo);
        if (moduleAt == null)
            return new Variable(null);

        final FunctionExecutable function = moduleAt.getFunctionByName(name);
        return new Variable(new BindingFunctionWrapper(_computerData, moduleAt, _slotNo, function));
    }
}
