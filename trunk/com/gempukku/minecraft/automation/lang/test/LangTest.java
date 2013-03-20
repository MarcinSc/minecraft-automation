package com.gempukku.minecraft.automation.lang.test;

import com.gempukku.minecraft.automation.lang.*;
import com.gempukku.minecraft.automation.lang.statement.*;

import java.util.Arrays;
import java.util.Collections;

public class LangTest {
    public static void main(String[] args) throws IllegalSyntaxException {
        ScriptExecutable exec = new ScriptExecutable();
        FunctionExecutable function = new FunctionExecutable(new String[]{"param"});
        function.setStatement(
                new BlockStatement(
                        Arrays.asList(
                                new ReturnStatement(
                                        new ArithmeticStatement(
                                                Arrays.asList(
                                                        new VariableStatement("param"), new ConstantStatement(new Variable(" from function"))),
                                                Arrays.asList(
                                                        ArithmeticStatement.Oper.ADD))),
                                new ExecutableStatement() {
                                    public Execution createExecution() {
                                        throw new RuntimeException("Should never get here");
                                    }
                                }
                        )));
        exec.addFunction("func", function);

        exec.setStatement(
                new BlockStatement(
                        Arrays.<ExecutableStatement>asList(
                                new AssignStatement(true, "val", new ConstantStatement(new Variable("Hello world"))),
                                new AssignStatement(true, "result",
                                        new CallFunctionStatement("func",
                                                Collections.<ExecutableStatement>singletonList(
                                                        new VariableStatement("val")
                                                )))
                        )));

        for (int i = 0; i < 100000; i++)
            executeScript(exec);

        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++)
            executeScript(exec);

        System.out.println("time: " + (System.currentTimeMillis() - start));

    }

    private static void executeScript(ScriptExecutable exec) throws IllegalSyntaxException {
        CallContext context = new CallContext();
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.stackFunctionCall(context, exec.createExecution(context));

        while (!executionContext.isFinished())
            executionContext.executeNext();
    }
}
