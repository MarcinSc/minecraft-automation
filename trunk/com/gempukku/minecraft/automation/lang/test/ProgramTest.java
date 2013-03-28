package com.gempukku.minecraft.automation.lang.test;

import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.parser.ScriptParser;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

public class ProgramTest {
    public static void main(String[] args) throws ExecutionException, IOException, IllegalSyntaxException {
        String script = helloWorldScript();

        ScriptParser parser = new ScriptParser();
        final ScriptExecutable scriptExecutable = parser.parseScript(new StringReader(script));
        executeScript(scriptExecutable);
    }

    private static void executeScript(ScriptExecutable exec) throws ExecutionException {
        CallContext context = new CallContext(null, false, true);
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.stackExecutionGroup(context, exec.createExecution(context));

        while (!executionContext.isFinished())
            executionContext.executeNext();

        System.out.println(executionContext.getContextValue().getValue());
    }

    private static String helloWorldScript() {
        final StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        writer.println("function echo(toReturn) {");
        writer.println("  var result;");
        writer.println("  if (true) result = toReturn + \" \" + (-123 + 2 * -2) +\" \"+ (0 < 0);");
        writer.println("  return result;");
        writer.println("}");
        writer.println("if (123<122.5) { return echo(\"What?!\"); }");
        writer.println("var nr = 12.4;");
        writer.println("return echo(\"Hello world\");");
        return out.toString();
    }
}
