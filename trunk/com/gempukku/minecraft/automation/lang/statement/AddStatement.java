package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.ExecutableStatement;
import com.gempukku.minecraft.automation.lang.Execution;
import com.gempukku.minecraft.automation.lang.execution.AddExecution;

public class AddStatement implements ExecutableStatement {
    private ExecutableStatement _left;
    private ExecutableStatement _right;

    public AddStatement(ExecutableStatement left, ExecutableStatement right) {
        _left = left;
        _right = right;
    }

    @Override
    public Execution createExecution() {
        return new AddExecution(_left, _right);
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
