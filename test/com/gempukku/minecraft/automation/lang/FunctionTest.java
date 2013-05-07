package com.gempukku.minecraft.automation.lang;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FunctionTest extends ProgramTest {
    @Test
    public void functionCalled() throws IllegalSyntaxException, ExecutionException {
        final Variable variable = executeScript("function func() { return \"Test\"; } return func();");
        assertEquals("Test", variable.getValue());
    }

    @Test
    public void functionNotCalled() throws IllegalSyntaxException, ExecutionException {
        final Variable variable = executeScript("function func() { return \"Failed\"; } var v = \"Test\"; return v;");
        assertEquals("Test", variable.getValue());
    }

    @Test
    public void parameterGetsPassed() throws IllegalSyntaxException, ExecutionException {
        final Variable variable = executeScript("function func(value) { return value; } return func(\"Test\");");
        assertEquals("Test", variable.getValue());
    }

    @Test
    public void multipleParameters() throws IllegalSyntaxException, ExecutionException {
        final Variable variable = executeScript("function func(v, v2) { if (v2) return v; return \"Failed\"; } return func(\"Test\", true);");
        assertEquals("Test", variable.getValue());
    }

    @Test
    public void functionHasItsOwnScope() throws IllegalSyntaxException, ExecutionException {
        final Variable variable = executeScript("var v = \"Failed\"; function func() { var v; if (v==null) return \"Test\"; } return func();");
        assertEquals("Test", variable.getValue());
    }

    @Test
    public void variablesDefinedInFunctionAreNotVisibleOutside() throws ExecutionException {
        try {
            executeScript("function func() { var v = \"Test\"; } func(); if (v != null) return \"Failed\";");
            fail("Expected IllegalSyntaxException");
        } catch (IllegalSyntaxException exp) {
            // Expected
        }
    }

    @Test
    public void recursiveFunctionCall() throws IllegalSyntaxException, ExecutionException {
        final Variable variable = executeScript("function factorial(n) { if (n == 0) { return 1; } return n * factorial(n - 1); } return factorial(3);");
        assertEquals(6, ((Number) variable.getValue()).intValue());
    }

    @Test
    public void recursiveFunctionCall2() throws IllegalSyntaxException, ExecutionException {
        final Variable variable = executeScript("var factorial = function(n) { if (n == 0) { return 1; } return n * factorial(n - 1); }; return factorial(3);");
        assertEquals(6, ((Number) variable.getValue()).intValue());
    }

    @Test
    public void testClosure() throws IllegalSyntaxException, ExecutionException {
        final Variable variable = executeScript("var displayClosure = function() { var count = 0; return function () { return ++count; }; };"
                + " var inc = displayClosure(); return \"\"+inc()+\" \"+inc()+\" \"+inc();");
        assertEquals("1.0 2.0 3.0", variable.getValue());
    }

    @Test
    public void anonymousFunction() throws IllegalSyntaxException, ExecutionException {
        final Variable variable = executeScript("var v; v = 1; var getValue = (function(v) { return function() {return v;}; }(v)); v = 2; return getValue();");
        assertEquals(1, ((Number) variable.getValue()).intValue());
    }
}
