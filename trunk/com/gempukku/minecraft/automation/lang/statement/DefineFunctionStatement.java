package com.gempukku.minecraft.automation.lang.statement;

import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.execution.SimpleExecution;

import java.util.List;

public class DefineFunctionStatement implements ExecutableStatement {
    private String _name;
    private List<String> _parameterNames;
    private List<ExecutableStatement> _statements;

    public DefineFunctionStatement(String name, List<String> parameterNames, List<ExecutableStatement> statements) {
        _name = name;
        _parameterNames = parameterNames;
        _statements = statements;
    }

    @Override
    public Execution createExecution() {
        return new SimpleExecution() {
            @Override
            protected ExecutionProgress execute(ExecutionContext context) throws ExecutionException {
                final FunctionExecutable functionExecutable = new FunctionExecutable(_parameterNames.toArray(new String[_parameterNames.size()]));
                functionExecutable.setStatement(
                        new BlockStatement(_statements, false, true));
                final CallContext callContext = context.peekCallContext();
                callContext.defineVariable(_name);
                callContext.setVariableValue(_name, functionExecutable);
                return new ExecutionProgress(100);
            }
        };
    }

    @Override
    public boolean requiresSemicolon() {
        return false;
    }
}
