package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.ExecutableStatement;
import com.gempukku.minecraft.automation.lang.Execution;
import com.gempukku.minecraft.automation.lang.execution.FunctionCallExecution;

import java.util.List;

public class FunctionCallStatement implements ExecutableStatement {
    private String _name;
    private List<ExecutableStatement> _parameters;

    public FunctionCallStatement(String name, List<ExecutableStatement> parameters) {
        _name = name;
        _parameters = parameters;
    }

    @Override
    public Execution createExecution() {
        return new FunctionCallExecution(_name, _parameters);
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
