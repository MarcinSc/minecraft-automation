package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.ExecutableStatement;
import com.gempukku.minecraft.automation.lang.Execution;
import com.gempukku.minecraft.automation.lang.execution.AssignExecution;

public class AssignStatement implements ExecutableStatement {
    private boolean _define;
    private String _name;
    private ExecutableStatement _value;

    public AssignStatement(boolean define, String name, ExecutableStatement value) {
        _define = define;
        _name = name;
        _value = value;
    }

    @Override
    public Execution createExecution() {
        return new AssignExecution(_define, _name, _value);
    }

    @Override
    public boolean requiresSemicolon() {
        return true;
    }
}
