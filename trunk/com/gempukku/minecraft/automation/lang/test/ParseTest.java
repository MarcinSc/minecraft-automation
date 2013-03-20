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
        final StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);
        writer.println("var value = \"Hello world\";");
        writer.println("var result = func(value);");
        writer.println("function func(param) {");
        writer.println("  return param + \" from function\";");
        writer.println("}");

        ScriptParser parser = new ScriptParser();
        final ScriptExecutable scriptExecutable = parser.parseScript(new StringReader(out.toString()));
    }
}
