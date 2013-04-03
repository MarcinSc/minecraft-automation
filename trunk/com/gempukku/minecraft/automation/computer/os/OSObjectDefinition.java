package com.gempukku.minecraft.automation.computer.os;

import com.gempukku.minecraft.automation.lang.FunctionExecutable;
import com.gempukku.minecraft.automation.lang.ObjectDefinition;
import com.gempukku.minecraft.automation.lang.Variable;

public class OSObjectDefinition implements ObjectDefinition {
    private FunctionExecutable _bindModule = new BindModuleFunction();
    private FunctionExecutable _exit = new ExitFunction();

    @Override
    public Variable getMember(String name) {
        if (name.equals("bindModule"))
            return new Variable(_bindModule);
        else if (name.equals("exit"))
            return new Variable(_exit);
        
        return null;
    }
}
