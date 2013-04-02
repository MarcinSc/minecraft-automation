package com.gempukku.minecraft.automation.lang;

import com.gempukku.minecraft.automation.lang.parser.ScriptParser;

import java.io.IOException;
import java.io.StringReader;

public class ProgramTest {
    protected Variable executeScript(ScriptExecutable exec) throws ExecutionException {
        CallContext context = new CallContext(null, false, true);
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.stackExecutionGroup(context, exec.createExecution(context));

        while (!executionContext.isFinished())
            executionContext.executeNext();

        return executionContext.getReturnValue();
    }

    protected Variable executeScript(String script) throws IllegalSyntaxException, IOException, ExecutionException {
        ScriptExecutable exec = new ScriptParser().parseScript(new StringReader(script));
        return executeScript(exec);
    }
}
