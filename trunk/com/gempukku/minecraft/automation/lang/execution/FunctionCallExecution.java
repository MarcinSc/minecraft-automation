package com.gempukku.minecraft.automation.lang.execution;

import com.gempukku.minecraft.automation.lang.*;

import java.util.ArrayList;
import java.util.List;

public class FunctionCallExecution implements Execution {
    private String _name;
    private List<ExecutableStatement> _parameters;

    private int _nextParameterIndexStacked;
    private int _nextParameterValueStored;
    private boolean _functionCalled;
    private boolean _returnResultRead;

    private List<Variable> _parameterValues = new ArrayList<Variable>();

    public FunctionCallExecution(String name, List<ExecutableStatement> parameters) {
        _name = name;
        _parameters = parameters;
    }

    @Override
    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (_nextParameterValueStored < _nextParameterIndexStacked)
            return true;
        if (_nextParameterIndexStacked < _parameters.size())
            return true;
        if (!_functionCalled)
            return true;
        if (!_returnResultRead)
            return true;
        return false;
    }

    @Override
    public ExecutionProgress executeNextStatement(ExecutionContext executionContext) throws ExecutionException {
        if (_nextParameterValueStored < _nextParameterIndexStacked) {
            _parameterValues.add(executionContext.getContextValue());
            _nextParameterValueStored++;
            return new ExecutionProgress(100);
        }
        if (_nextParameterIndexStacked < _parameters.size()) {
            executionContext.stackExecution(_parameters.get(_nextParameterIndexStacked).createExecution());
            _nextParameterIndexStacked++;
            return new ExecutionProgress(100);
        }
        if (!_functionCalled) {
            final CallContext currentContext = executionContext.peekCallContext();
            final FunctionExecutable function = currentContext.getFunction(_name);
            final CallContext functionContextParent = currentContext.getContextForFunction(_name);
            final String[] parameterNames = function.getParameterNames();
            if (_parameterValues.size() > parameterNames.length)
                throw new ExecutionException("Function does not accept as many parameters");

            CallContext functionContext = new CallContext(functionContextParent, false, true);
            for (int i = 0; i < parameterNames.length; i++) {
                functionContext.defineVariable(parameterNames[i]);
                if (i < _parameterValues.size())
                    functionContext.setVariableValue(parameterNames[i], _parameterValues.get(i).getValue());
            }
            executionContext.stackExecutionGroup(functionContext, function.createExecution(functionContext));
            _functionCalled = true;
            return new ExecutionProgress(100);
        }
        if (!_returnResultRead) {
            final Variable returnValue = executionContext.getReturnValue();
            executionContext.setContextValue(returnValue);
            executionContext.resetReturnValue();
            _returnResultRead = true;
            return new ExecutionProgress(100);
        }
        return null;
    }
}
