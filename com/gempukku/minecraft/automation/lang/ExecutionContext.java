package com.gempukku.minecraft.automation.lang;

import java.util.*;

public class ExecutionContext {
    private LinkedList<LinkedList<Execution>> _executionGroups = new LinkedList<LinkedList<Execution>>();
    private Variable _contextValue;
    private Variable _returnValue;

    private boolean _returnFromFunction;
    private boolean _breakFromBlock;

    private LinkedList<CallContext> _groupCallContexts = new LinkedList<CallContext>();
    private Map<Variable.Type, PropertyProducer> _perTypeProperties = new HashMap<Variable.Type, PropertyProducer>();

    private int _stackTraceSize = 0;

    public int getStackTraceSize() {
        return _stackTraceSize;
    }

    public int getMemoryUsage() {
        Set<Object> counted = new HashSet<Object>();
        int result = 0;
        for (CallContext groupCallContext : _groupCallContexts) {
            for (Variable variable : groupCallContext.getVariablesInContext()) {
                final Object value = variable.getValue();
                if (!counted.contains(value)) {
                    counted.add(value);
                    result += sizeOf(counted, value);
                }
            }
        }
        return result;
    }

    private int sizeOf(Set<Object> counted, Object value) {
        if (value == null) {
            return 1;
        } else if (value instanceof String) {
            return ((String) value).length();
        } else if (value instanceof Number) {
            return 4;
        } else if (value instanceof Map) {
            Map<String, Variable> map = (Map<String, Variable>) value;
            int result = 4;
            for (Variable variable : map.values()) {
                Object mapValue = variable.getValue();
                if (!counted.contains(mapValue)) {
                    counted.add(mapValue);
                    result += sizeOf(counted, mapValue);
                }
            }
            return result;
        } else if (value instanceof Boolean) {
            return 1;
        } else if (value instanceof FunctionExecutable) {
            return 4;
        } else if (value instanceof ObjectDefinition) {
            return 4;
        } else if (value.getClass().isArray()) {
            return 4;
        } else
            throw new UnsupportedOperationException("Unknown type of variable value: " + value.getClass().getSimpleName());
    }

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
            removeLastCallContext();
        }
        return null;
    }

    private CallContext removeLastCallContext() {
        final CallContext removedCallContext = _groupCallContexts.removeLast();
        if (removedCallContext.isConsumesReturn())
            _stackTraceSize--;
        return removedCallContext;
    }

    private void doTheBreak() throws ExecutionException {
        CallContext callContext;
        do {
            if (_groupCallContexts.isEmpty())
                throw new ExecutionException("break invoked without a containing block");
            callContext = removeLastCallContext();
            _executionGroups.removeLast();
        } while (!callContext.isConsumesBreak());
        _breakFromBlock = false;
    }

    private void doTheReturn() {
        CallContext callContext;
        do {
            callContext = removeLastCallContext();
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

    public void breakBlock() {
        _breakFromBlock = true;
    }

    public void resetReturnValue() {
        _returnValue = null;
        _returnFromFunction = false;
    }

    public CallContext peekCallContext() {
        return _groupCallContexts.getLast();
    }

    public void setVariableValue(Variable variable, Object value) throws ExecutionException {
        variable.setValue(value);
    }

    public void stackExecutionGroup(CallContext callContext, Execution execution) {
        _groupCallContexts.add(callContext);
        LinkedList<Execution> functionExecutionStack = new LinkedList<Execution>();
        functionExecutionStack.add(execution);
        _executionGroups.add(functionExecutionStack);
        if (callContext.isConsumesReturn())
            _stackTraceSize++;
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
