package com.gempukku.minecraft.automation.lang;

import java.util.HashMap;
import java.util.Map;

public class CallContext {
    private CallContext _parentContext;
    private Map<String, FunctionExecutable> _functions = new HashMap<String, FunctionExecutable>();
    private Map<String, Variable> _variables = new HashMap<String, Variable>();
    private Variable _returnValue;

    public CallContext() {
        this(null);
    }

    public CallContext(CallContext parentContext) {
        _parentContext = parentContext;
    }

    public CallContext getParentContext() {
        return _parentContext;
    }

    public Variable getReturnValue() {
        return _returnValue;
    }

    public void setReturnValue(Variable returnValue) {
        _returnValue = returnValue;
    }

    public void addFunction(String functionName, FunctionExecutable executable) {
        _functions.put(functionName, executable);
    }

    public Variable getVariableValue(String name) throws IllegalSyntaxException {
        final Variable variable = _variables.get(name);
        if (variable != null)
            return variable;
        else if (_parentContext != null)
            return _parentContext.getVariableValue(name);
        else
            throw new IllegalSyntaxException("Variable with this name is not defined in this scope: " + name);
    }

    public void defineVariable(String name) throws IllegalSyntaxException {
        final Variable variable = _variables.get(name);
        if (variable == null)
            _variables.put(name, new Variable(null));
        else
            throw new IllegalSyntaxException("Variable with this name is already defined in this scope: " + name);
    }

    public void setVariableValue(String name, Object value) throws IllegalSyntaxException {
        final Variable variable = _variables.get(name);
        if (variable != null)
            variable.setValue(value);
        else if (_parentContext != null)
            _parentContext.setVariableValue(name, value);
        else
            throw new IllegalSyntaxException("Variable with this name is not defined in this scope: " + name);
    }

    public FunctionExecutable getFunction(String name) throws IllegalSyntaxException {
        final FunctionExecutable functionExecutable = _functions.get(name);
        if (functionExecutable != null)
            return functionExecutable;
        else if (_parentContext != null)
            return _parentContext.getFunction(name);
        else
            throw new IllegalSyntaxException("Function with this name is not defined in this scope: " + name);
    }

    public CallContext getContextForFunction(String name) throws IllegalSyntaxException {
        if (_functions.containsKey(name))
            return this;
        else if (_parentContext != null)
            return _parentContext.getContextForFunction(name);
        else
            throw new IllegalSyntaxException("Function doesn't seem to have a context");
    }
}
