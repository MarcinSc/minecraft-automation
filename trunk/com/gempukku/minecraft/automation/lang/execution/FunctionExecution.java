package com.gempukku.minecraft.automation.lang.execution;

import com.gempukku.minecraft.automation.lang.*;

import java.util.ArrayList;
import java.util.List;

public class FunctionExecution implements Execution {
    private String _functionName;
    private List<ExecutableStatement> _parameterExecutions;

    private List<Variable> _parameterValues =new ArrayList<Variable>();

    private int _nextParameterExecutionIndex = 0;
    private int _retrievedParameterValueIndex = 0;
    private boolean _stackedFunctionCall = false;

    public FunctionExecution(final String functionName, List<ExecutableStatement> parameterExecutions) {
        _functionName = functionName;
        _parameterExecutions = parameterExecutions;
    }

    public boolean hasNextExecution(ExecutionContext executionContext) {
        if (_nextParameterExecutionIndex < _parameterExecutions.size())
            return true;
        if (_retrievedParameterValueIndex< _parameterExecutions.size())
            return true;
        if (!_stackedFunctionCall)
            return true;
        return false;
    }

    public ExecutionProgress executeNextStatement(ExecutionContext context) throws IllegalSyntaxException {
        if (_retrievedParameterValueIndex < _nextParameterExecutionIndex) {
            _parameterValues.add(context.getContextValue());
            _retrievedParameterValueIndex++;
            return new ExecutionProgress(100);
        }
        if (_nextParameterExecutionIndex < _parameterExecutions.size()) {
            final ExecutableStatement executableStatement = _parameterExecutions.get(_nextParameterExecutionIndex);
            final Execution execution = executableStatement.createExecution();
            context.stackExecution(execution);
            _nextParameterExecutionIndex++;
            return new ExecutionProgress(100);
        }
        if (!_stackedFunctionCall) {
            final CallContext callContext = context.peekCallContext();
            final FunctionExecutable function = context.peekCallContext().getFunction(_functionName);
            final CallContext parentContext = callContext.getContextForFunction(_functionName);
            
            CallContext functionContext = new CallContext(parentContext);
            functionContext.setFunctionContext(true);
            final String[] parameterNames = function.getParameterNames();
            for (int i=0; i<parameterNames.length; i++) {
                final String name = parameterNames[i];
                functionContext.defineVariable(name);
                Object value = null;
                if (i<_parameterValues.size())
                    value = _parameterValues.get(i).getValue();
                functionContext.setVariableValue(name, value);
            }
            context.stackBlockCall(functionContext, function.createExecution(functionContext));
            _stackedFunctionCall = true;
            return new ExecutionProgress(100);
        }
        throw new IllegalStateException("Should not get here");
    }
}
