package com.gempukku.minecraft.automation.lang.test;

import com.gempukku.minecraft.automation.lang.CallContext;
import com.gempukku.minecraft.automation.lang.ExecutionContext;
import com.gempukku.minecraft.automation.lang.IllegalSyntaxException;
import com.gempukku.minecraft.automation.lang.ScriptExecutable;
import com.gempukku.minecraft.automation.lang.parser.ScriptParser;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

public class ProgramTest {
    public static void main(String[] args) throws IllegalSyntaxException, IOException {
        String script = helloWorldScript();

        ScriptParser parser = new ScriptParser();
        final ScriptExecutable scriptExecutable = parser.parseScript(new StringReader(script));
        executeScript(scriptExecutable);
    }

    private static void executeScript(ScriptExecutable exec) throws IllegalSyntaxException {
        CallContext context = new CallContext();
        context.setFunctionContext(true);
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.stackBlockCall(context, exec.createExecution(context));

        while (!executionContext.isFinished())
            executionContext.executeNext();

        System.out.println(executionContext.getContextValue().getValue());
    }

    private static String helloWorldScript() {
        final StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        writer.println("var value = \"Hello world\";");
        writer.println("var replace = \"Replaced hello world\";");
        writer.println("while (true) value=replace;");
        writer.println("return value;");
        return out.toString();
    }
}
