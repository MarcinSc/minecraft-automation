package com.gempukku.minecraft.automation.lang;

import java.util.LinkedList;

public class ExecutionContext {
    private LinkedList<LinkedList<Execution>> _executionsInFunctions = new LinkedList<LinkedList<Execution>>();
    private Variable _contextValue;
    private LinkedList<CallContext> _callContexts = new LinkedList<CallContext>();

    public void stackExecution(Execution execution) {
        _executionsInFunctions.getLast().add(execution);
    }

    public ExecutionProgress executeNext() throws IllegalSyntaxException {
        while (!_executionsInFunctions.isEmpty()) {
            final LinkedList<Execution> inFunctionExecutionStack = _executionsInFunctions.getLast();
            while (!inFunctionExecutionStack.isEmpty()) {
                final Execution execution = inFunctionExecutionStack.getLast();
                if (execution.hasNextExecution(this)) {
                    final ExecutionProgress executionProgress = execution.executeNextStatement(this);
                    if (_callContexts.getLast().getReturnValue() != null)
                        exitFunction();
                    return executionProgress;
                } else
                    inFunctionExecutionStack.removeLast();
            }
            exitFunction();
        }
        return null;
    }

    private void exitFunction() {
        _executionsInFunctions.removeLast();
        final Variable returnValue = _callContexts.removeLast().getReturnValue();
        setContextValue(returnValue);
    }

    public Variable getContextValue() {
        return _contextValue;
    }

    public void setContextValue(Variable contextValue) {
        _contextValue = contextValue;
    }

    public CallContext peekCallContext() {
        return _callContexts.getLast();
    }

    public void stackFunctionCall(CallContext callContext, Execution execution) {
        _callContexts.add(callContext);
        LinkedList<Execution> functionExecutionStack = new LinkedList<Execution>();
        functionExecutionStack.add(execution);
        _executionsInFunctions.add(functionExecutionStack);
    }

    public boolean isFinished() {
        return _executionsInFunctions.isEmpty();
    }
}
