package com.gempukku.minecraft.automation.lang;

import java.util.HashMap;
import java.util.Map;

public class ScriptExecutable {
    private Map<String, FunctionExecutable> _functions = new HashMap<String, FunctionExecutable>();
    private ExecutableStatement _statement;

    public void addFunction(String name, FunctionExecutable function) throws IllegalSyntaxException {
        if (_functions.containsKey(name))
            throw new IllegalSyntaxException("Function with name "+name+" already exists in the context");
        _functions.put(name, function);
    }

    public void setStatement(ExecutableStatement statement) {
        _statement = statement;
    }

    public Execution createExecution(CallContext context) {
        for (Map.Entry<String, FunctionExecutable> functionDef: _functions.entrySet())
            context.addFunction(functionDef.getKey(), functionDef.getValue());

        return _statement.createExecution();
    }
}
