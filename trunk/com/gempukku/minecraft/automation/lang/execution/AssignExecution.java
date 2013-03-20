package com.gempukku.minecraft.automation.lang.execution;

import com.gempukku.minecraft.automation.lang.*;

public class AssignExecution implements Execution {
    private boolean _define;
    private String _name;
    private ExecutableStatement _value;

    private boolean _stackedValueCall = false;
    private boolean _madeAssignment = false;

    public AssignExecution(boolean define, String name, ExecutableStatement value) {
        _define = define;
        _name = name;
        _value = value;
    }

    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (!_stackedValueCall)
            return true;
        if (!_madeAssignment)
            return true;
        return false;
    }

    public ExecutionProgress executeNextStatement(ExecutionContext executionContext) throws IllegalSyntaxException {
        if (!_stackedValueCall) {
            executionContext.stackExecution(_value.createExecution());
            _stackedValueCall = true;
            return new ExecutionProgress(100);
        }
        if (!_madeAssignment) {
            if (_define)
                executionContext.peekCallContext().defineVariable(_name);
            executionContext.peekCallContext().setVariableValue(_name, executionContext.getContextValue().getValue());
            _madeAssignment = true;
            return new ExecutionProgress(100);
        }
        throw new IllegalStateException("Should not get here");
    }
}
