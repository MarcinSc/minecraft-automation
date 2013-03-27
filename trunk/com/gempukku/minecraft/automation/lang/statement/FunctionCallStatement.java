package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.ExecutableStatement;
import com.gempukku.minecraft.automation.lang.Execution;
import com.gempukku.minecraft.automation.lang.execution.FunctionCallExecution;

import java.util.List;

public class FunctionCallStatement implements ExecutableStatement {
    private ExecutableStatement _function;
    private List<ExecutableStatement> _parameters;

    public FunctionCallStatement(ExecutableStatement function, List<ExecutableStatement> parameters) {
        _function = function;
        _parameters = parameters;
    }

    @Override
    public Execution createExecution() {
        return new FunctionCallExecution(_function, _parameters);
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
