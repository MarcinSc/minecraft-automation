package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.ExecutableStatement;
import com.gempukku.minecraft.automation.lang.Execution;
import com.gempukku.minecraft.automation.lang.execution.MapAccessExecution;

public class MapAccessStatement implements ExecutableStatement {
    private ExecutableStatement _mapStatement;
    private ExecutableStatement _propertyStatement;

    public MapAccessStatement(ExecutableStatement mapStatement, ExecutableStatement propertyStatement) {
        _mapStatement = mapStatement;
        _propertyStatement = propertyStatement;
    }

    @Override
    public Execution createExecution() {
        return new MapAccessExecution(_mapStatement, _propertyStatement);
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
