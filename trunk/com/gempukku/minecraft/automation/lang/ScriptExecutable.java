package com.gempukku.minecraft.automation.lang;

import com.gempukku.minecraft.automation.lang.execution.MultiStatementExecution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptExecutable {
    private Map<String, FunctionExecutable> _functions = new HashMap<String, FunctionExecutable>();
    private List<ExecutableStatement> _statements = new ArrayList<ExecutableStatement>();

    public void addFunction(String name, FunctionExecutable function) throws IllegalSyntaxException {
        if (_functions.containsKey(name))
            throw new IllegalSyntaxException("Function with name "+name+" already exists in the context");
        _functions.put(name, function);
    }

    public void appendStatement(ExecutableStatement statement) {
        _statements.add(statement);
    }

    public Execution createExecution(CallContext context) {
        for (Map.Entry<String, FunctionExecutable> functionDef: _functions.entrySet())
            context.addFunction(functionDef.getKey(), functionDef.getValue());

        return new MultiStatementExecution(_statements);
    }
}
