package com.gempukku.minecraft.automation.lang;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ExecutionContext {
    private LinkedList<LinkedList<Execution>> _executionGroups = new LinkedList<LinkedList<Execution>>();
    private Variable _contextValue;
    private Variable _returnValue;

    private boolean _returnFromFunction;
    private boolean _breakFromBlock;

    private LinkedList<CallContext> _groupCallContexts = new LinkedList<CallContext>();
    private Map<Variable.Type, PropertyProducer> _perTypeProperties = new HashMap<Variable.Type, PropertyProducer>();

    public void stackExecution(Execution execution) {
        _executionGroups.getLast().add(execution);
    }

    public ExecutionProgress executeNext() throws ExecutionException {
        while (!_executionGroups.isEmpty()) {
            final LinkedList<Execution> inBlockExecutionStack = _executionGroups.getLast();
            while (!inBlockExecutionStack.isEmpty()) {
                final Execution execution = inBlockExecutionStack.getLast();
                if (execution.hasNextExecution(this)) {
                    final ExecutionProgress executionProgress = execution.executeNextStatement(this);
                    if (_breakFromBlock)
                        doTheBreak();
                    if (_returnFromFunction)
                        doTheReturn();
                    return executionProgress;
                } else
                    inBlockExecutionStack.removeLast();
            }
            _executionGroups.removeLast();
            _groupCallContexts.removeLast();
        }
        return null;
    }

    private void doTheBreak() {
        CallContext callContext;
        do {
            callContext = _groupCallContexts.removeLast();
            _executionGroups.removeLast();
        } while (!callContext.isConsumesBreak());
    }

    private void doTheReturn() {
        CallContext callContext;
        do {
            callContext = _groupCallContexts.removeLast();
            _executionGroups.removeLast();
        } while (!callContext.isConsumesReturn());
    }

    public Variable getContextValue() {
        return _contextValue;
    }

    public void setContextValue(Variable contextValue) {
        _contextValue = contextValue;
    }

    public Variable getReturnValue() {
        return _returnValue;
    }

    public void setReturnValue(Variable returnValue) {
        _returnValue = returnValue;
        _returnFromFunction = true;
    }

    public void resetReturnValue() {
        _returnValue = null;
        _returnFromFunction = false;
    }

    public CallContext peekCallContext() {
        return _groupCallContexts.getLast();
    }

    public void stackExecutionGroup(CallContext callContext, Execution execution) {
        _groupCallContexts.add(callContext);
        LinkedList<Execution> functionExecutionStack = new LinkedList<Execution>();
        functionExecutionStack.add(execution);
        _executionGroups.add(functionExecutionStack);
    }

    public boolean isFinished() {
        return _executionGroups.isEmpty();
    }

    public void addPropertyProducer(Variable.Type type, PropertyProducer producer) {
        _perTypeProperties.put(type, producer);
    }

    public Variable resolveMember(Variable object, String property) throws ExecutionException {
        if (!_perTypeProperties.containsKey(object.getType()))
            throw new ExecutionException("Expected object");

        return _perTypeProperties.get(object.getType()).exposePropertyFor(object, property);
    }
}
