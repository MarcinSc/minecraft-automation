package com.gempukku.minecraft.automation.lang.test;

import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.statement.*;

import java.util.Collections;

public class LangTest {
    public static void main(String[] args) throws IllegalSyntaxException {
        ScriptExecutable exec = new ScriptExecutable();
        FunctionExecutable function = new FunctionExecutable(new String[]{"param"});
        function.appendStatement(
                new ReturnStatement(
                        new VariableStatement("param")));
        exec.addFunction("func", function);

        exec.appendStatement(
                new AssignStatement(true, "val", new ConstantStatement(new Variable("Hello world"))));
        exec.appendStatement(
                new AssignStatement(true, "result",
                        new CallFunctionStatement("func",
                                Collections.<ExecutableStatement>singletonList(
                                        new VariableStatement("val")
                                ))));

        for (int i=0; i<100000; i++)
            executeScript(exec);

        long start = System.currentTimeMillis();
        for (int i=0; i<100000; i++)
            executeScript(exec);
        
        System.out.println("time: "+(System.currentTimeMillis()-start));

    }

    private static void executeScript(ScriptExecutable exec) throws IllegalSyntaxException {
        CallContext context = new CallContext();
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.stackFunctionCall(context, exec.createExecution(context));

        while (!executionContext.isFinished())
            executionContext.executeNext();

//        System.out.println(context.getVariableValue("result").getValue());
    }
}
