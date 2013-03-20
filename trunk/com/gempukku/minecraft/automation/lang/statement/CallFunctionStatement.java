package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.ExecutableStatement;
import com.gempukku.minecraft.automation.lang.Execution;
import com.gempukku.minecraft.automation.lang.execution.FunctionExecution;

import java.util.List;

public class CallFunctionStatement implements ExecutableStatement {
    private String _functionName;
    private List<ExecutableStatement> _parameterStatements;

    public CallFunctionStatement(String functionName, List<ExecutableStatement> parameterStatements) {
        _functionName = functionName;
        _parameterStatements = parameterStatements;
    }

    public Execution createExecution() {
        return new FunctionExecution(_functionName, _parameterStatements);
    }
}
