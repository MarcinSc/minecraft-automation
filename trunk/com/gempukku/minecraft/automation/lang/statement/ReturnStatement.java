package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.ExecutableStatement;
import com.gempukku.minecraft.automation.lang.Execution;
import com.gempukku.minecraft.automation.lang.execution.ReturnExecution;

public class ReturnStatement implements ExecutableStatement {
    private ExecutableStatement _result;

    public ReturnStatement(ExecutableStatement result) {
        _result = result;
    }

    @Override
    public Execution createExecution() {
        return new ReturnExecution(_result);
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
