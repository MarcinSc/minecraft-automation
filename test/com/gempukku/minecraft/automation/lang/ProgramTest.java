package com.gempukku.minecraft.automation.lang;

import com.gempukku.minecraft.automation.computer.ComputerData;
import com.gempukku.minecraft.automation.computer.MinecraftComputerExecutionContext;
import com.gempukku.minecraft.automation.lang.parser.ScriptParser;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class ProgramTest {
    protected Variable executeScript(ScriptExecutable exec) throws ExecutionException {
        CallContext context = new CallContext(null, false, true);
        ExecutionContext executionContext = initExecutionContext();
        executionContext.stackExecutionGroup(context, exec.createExecution(context));

        while (!executionContext.isFinished())
            executionContext.executeNext();

        assertEquals(4, executionContext.getMemoryUsage());
        assertEquals(0, executionContext.getStackTraceSize());

        return executionContext.getReturnValue();
    }

    protected ExecutionContext initExecutionContext() {
        ExecutionContext executionContext = new MinecraftComputerExecutionContext(new ComputerData(0, "owner"));
        executionContext.addPropertyProducer(Variable.Type.MAP, new MapPropertyProducer());
        executionContext.addPropertyProducer(Variable.Type.OBJECT, new ObjectPropertyProducer());
        return executionContext;
    }

    protected Variable executeScript(String script) throws IllegalSyntaxException, IOException, ExecutionException {
        ScriptExecutable exec = new ScriptParser().parseScript(new StringReader(script));
        return executeScript(exec);
    }
}
