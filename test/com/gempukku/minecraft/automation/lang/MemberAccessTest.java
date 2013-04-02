package com.gempukku.minecraft.automation.lang;

import com.gempukku.minecraft.automation.lang.parser.ScriptParser;
import com.gempukku.minecraft.automation.lang.statement.ConstantStatement;
import com.gempukku.minecraft.automation.lang.statement.ReturnStatement;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class MemberAccessTest extends ProgramTest {
    @Test
    public void testMemberAccess() throws ExecutionException, IllegalSyntaxException, IOException {
        ScriptExecutable exec = new ScriptParser().parseScript(new StringReader("return os.exit();"));
        CallContext context = new CallContext(null, false, true);

        ObjectDefinition os = constructOS();

        context.defineVariable("os");
        context.setVariableValue("os", os);

        ExecutionContext executionContext = new ExecutionContext();
        executionContext.stackExecutionGroup(context, exec.createExecution(context));

        while (!executionContext.isFinished())
            executionContext.executeNext();

        assertEquals(0, ((Number) executionContext.getContextValue().getValue()).intValue());
    }

    private ObjectDefinition constructOS() {
        ObjectDefinition os = new ObjectDefinition();
        FunctionExecutable exitFunction = new FunctionExecutable(new String[0]);
        exitFunction.setStatement(
                new ReturnStatement(new ConstantStatement(new Variable(0))));
        os.addMember("exit", exitFunction);
        return os;
    }
}
