package com.gempukku.minecraft.automation.computer.os;

import com.gempukku.minecraft.automation.lang.CallContext;
import com.gempukku.minecraft.automation.lang.Execution;
import com.gempukku.minecraft.automation.lang.FunctionExecutable;
import com.gempukku.minecraft.automation.lang.Variable;
import com.gempukku.minecraft.automation.lang.execution.ReturnExecution;
import com.gempukku.minecraft.automation.lang.statement.ConstantStatement;

public class ExitFunction implements FunctionExecutable {
    @Override
    public String[] getParameterNames() {
        return new String[0];
    }

    @Override
    public Execution createExecution(CallContext callContext) {
        return new ReturnExecution(new ConstantStatement(new Variable(0)));
    }
}
