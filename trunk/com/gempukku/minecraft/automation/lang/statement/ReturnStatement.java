package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.ExecutableStatement;
import com.gempukku.minecraft.automation.lang.Execution;
import com.gempukku.minecraft.automation.lang.execution.ReturnExecution;

public class ReturnStatement implements ExecutableStatement {
    private ExecutableStatement _value;

    public ReturnStatement(ExecutableStatement value) {
        _value = value;
    }

    public Execution createExecution() {
        return new ReturnExecution(_value);
    }
}
