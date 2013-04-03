package com.gempukku.minecraft.automation.computer.os;

import com.gempukku.minecraft.automation.lang.ObjectDefinition;
import com.gempukku.minecraft.automation.lang.Variable;

public class OSObjectDefinition implements ObjectDefinition {
    private Variable _bindModule = new Variable(new BindModuleFunction());
    private Variable _getModuleSlotCount = new Variable(new GetModuleSlotCountFunction());
    private Variable _getModuleName = new Variable(new GetModuleNameFunction());
    private Variable _exit = new Variable(new ExitFunction());

    @Override
    public Variable getMember(String name) {
        if (name.equals("bindModule"))
            return _bindModule;
        else if (name.equals("getModuleSlotCount"))
            return _getModuleSlotCount;
        else if (name.equals("getModuleType"))
            return _getModuleName;
        else if (name.equals("exit"))
            return _exit;
        
        return null;
    }
}
