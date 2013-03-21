package com.gempukku.minecraft.automation.lang.test;

import com.gempukku.minecraft.automation.lang.IllegalSyntaxException;
import com.gempukku.minecraft.automation.lang.ScriptExecutable;
import com.gempukku.minecraft.automation.lang.parser.ScriptParser;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

public class ParseTest {
    public static void main(String[] args) throws IllegalSyntaxException, IOException {
        //String script = helloWorldScript();
        String script = progressScript();

        ScriptParser parser = new ScriptParser();
        final ScriptExecutable scriptExecutable = parser.parseScript(new StringReader(script));
        System.out.println(scriptExecutable);
    }

    private static String progressScript() {
        final StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        writer.println("var variable = a;");
        writer.println("var string = \"Hello world\";");
        writer.println("var functionResult=func(value);");
        writer.println("var methodResult = object.method(param1, param2);");
        writer.println("var combinationResult = func(object.method(param1,param2)).method(param3);");

        return out.toString();
    }

    private static String helloWorldScript() {
        final StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        writer.println("var value = \"Hello world\"; var result = func(value);");
        writer.println("function func(param) {");
        writer.println("  return param + \" from function\";");
        writer.println("}");
        writer.println("for (var i=0; i<10; i++)");
        writer.println("os.print(result);");
        return out.toString();
    }
}
