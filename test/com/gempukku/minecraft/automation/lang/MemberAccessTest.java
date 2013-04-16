package com.gempukku.minecraft.automation.lang;

import com.gempukku.minecraft.automation.computer.os.OSObjectDefinition;
import com.gempukku.minecraft.automation.lang.parser.ScriptParser;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class MemberAccessTest extends ProgramTest {
    @Test
    public void testMemberAccess() throws ExecutionException, IllegalSyntaxException, IOException {
        ScriptExecutable exec = new ScriptParser().parseScript(new StringReader("return os.getModuleSlotCount();"));
        CallContext context = new CallContext(null, false, true);

        ObjectDefinition os = constructOS();

        Variable var = context.defineVariable("os");
        var.setValue(os);

        ExecutionContext executionContext = initExecutionContext();
        executionContext.stackExecutionGroup(context, exec.createExecution(context));

        while (!executionContext.isFinished())
            executionContext.executeNext();

        assertEquals(0, ((Number) executionContext.getReturnValue().getValue()).intValue());
    }

    private ObjectDefinition constructOS() {
        return new OSObjectDefinition();
    }
}
