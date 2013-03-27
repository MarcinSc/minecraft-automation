package com.gempukku.minecraft.automation.lang;

import java.util.HashMap;
import java.util.Map;

public class CallContext {
    private CallContext _parentContext;
    private boolean _consumesReturn;
    private boolean _consumesBreak;
    private Map<String, FunctionExecutable> _functions = new HashMap<String, FunctionExecutable>();
    private Map<String, Variable> _variables = new HashMap<String, Variable>();

    public CallContext(CallContext parentContext, boolean consumesBreak, boolean consumesReturn) {
        _parentContext = parentContext;
        _consumesBreak = consumesBreak;
        _consumesReturn = consumesReturn;
    }

    public CallContext getParentContext() {
        return _parentContext;
    }

    public boolean isConsumesBreak() {
        return _consumesBreak;
    }

    public boolean isConsumesReturn() {
        return _consumesReturn;
    }

    public void addFunction(String functionName, FunctionExecutable executable) {
        _functions.put(functionName, executable);
    }

    public Variable getVariableValue(String name) throws ExecutionException {
        final Variable variable = _variables.get(name);
        if (variable != null)
            return variable;
        else if (_parentContext != null)
            return _parentContext.getVariableValue(name);
        else
            throw new ExecutionException("Variable with this name is not defined in this scope: " + name);
    }

    public void defineVariable(String name) throws ExecutionException {
        final Variable variable = _variables.get(name);
        if (variable == null)
            _variables.put(name, new Variable(null));
        else
            throw new ExecutionException("Variable with this name is already defined in this scope: " + name);
    }

    public void setVariableValue(String name, Object value) throws ExecutionException {
        final Variable variable = _variables.get(name);
        if (variable != null)
            variable.setValue(value);
        else if (_parentContext != null)
            _parentContext.setVariableValue(name, value);
        else
            throw new ExecutionException("Variable with this name is not defined in this scope: " + name);
    }

    public FunctionExecutable getFunction(String name) throws ExecutionException {
        final FunctionExecutable functionExecutable = _functions.get(name);
        if (functionExecutable != null)
            return functionExecutable;
        else if (_parentContext != null)
            return _parentContext.getFunction(name);
        else
            throw new ExecutionException("Function with this name is not defined in this scope: " + name);
    }

    public CallContext getContextForFunction(String name) throws ExecutionException {
        if (_functions.containsKey(name))
            return this;
        else if (_parentContext != null)
            return _parentContext.getContextForFunction(name);
        else
            throw new ExecutionException("Function doesn't seem to have a context");
    }
}
