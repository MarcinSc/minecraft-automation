package com.gempukku.minecraft.automation.lang.execution;

import com.gempukku.minecraft.automation.lang.*;

public class AssignExecution implements Execution {
    private ExecutableStatement _variable;
    private ExecutableStatement _value;

    private boolean _stackedVariable;
    private boolean _extractedVariable;
    private boolean _stackedValue;
    private boolean _assignedValue;

    private Variable _variablePointer;

    public AssignExecution(ExecutableStatement variable, ExecutableStatement value) {
        _variable = variable;
        _value = value;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (!_stackedVariable)
            return true;
        if (!_extractedVariable)
            return true;
        if (!_stackedValue)
            return true;
        if (!_assignedValue)
            return true;
        return false;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext) throws ExecutionException {
        if (!_stackedVariable) {
            executionContext.stackExecution(_variable.createExecution());
            _stackedVariable = true;
            return new ExecutionProgress(100);
        }
        if (!_extractedVariable) {
            _variablePointer = executionContext.getContextValue();
            _extractedVariable = true;
            return new ExecutionProgress(100);
        }
        if (!_stackedValue) {
            executionContext.stackExecution(_value.createExecution());
            _stackedValue = true;
            return new ExecutionProgress(100);
        }
        if (!_assignedValue) {
            executionContext.setVariableValue(_variablePointer, executionContext.getContextValue().getValue());
            _assignedValue = true;
            return new ExecutionProgress(100);
        }
        return null;
    }
}
