package com.gempukku.minecraft.automation.lang.execution;

import com.gempukku.minecraft.automation.lang.*;

import java.util.ArrayList;
import java.util.List;

public class MethodCallExecution implements Execution {
    private ExecutableStatement _objectMethod;
    private String _methodName;
    private List<ExecutableStatement> _parameters;

    private boolean _stackedObject;
    private boolean _retrievedObject;
    private int _nextParameterIndex = 0;
    private int _nextParameterValueIndex = 0;
    private boolean _executed;

    private Variable _object;
    private List<Variable> _parameterValues = new ArrayList<Variable>();

    public MethodCallExecution(ExecutableStatement objectMethod, String methodName, List<ExecutableStatement> parameters) {
        _objectMethod = objectMethod;
        _methodName = methodName;
        _parameters = parameters;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (!_stackedObject)
            return true;
        if (!_retrievedObject)
            return true;
        if (_nextParameterValueIndex < _nextParameterIndex)
            return true;
        if (_nextParameterIndex < _parameters.size())
            return true;
        if (!_executed)
            return true;
        return false;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext) throws IllegalSyntaxException {
        if (!_stackedObject) {
            executionContext.stackExecution(_objectMethod.createExecution());
            _stackedObject = true;
            return new ExecutionProgress(100);
        }
        if (!_retrievedObject) {
            _object = executionContext.getContextValue();
            _retrievedObject = true;
            return new ExecutionProgress(100);
        }
        if (_nextParameterValueIndex < _nextParameterIndex) {
            _parameterValues.add(executionContext.getContextValue());
            _nextParameterValueIndex++;
            return new ExecutionProgress(100);
        }
        if (_nextParameterIndex < _parameters.size()) {
            executionContext.stackExecution(_parameters.get(_nextParameterIndex).createExecution());
            _nextParameterIndex++;
            return new ExecutionProgress(100);
        }
        if (!_executed) {
            ExecutionProgress result = executionContext.executeMethod(_object, _methodName, _parameterValues);
            _executed = true;
            return result;
        }
        throw new IllegalStateException("Should not get here");
    }
}
