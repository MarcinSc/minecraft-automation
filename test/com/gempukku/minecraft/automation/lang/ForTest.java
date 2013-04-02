package com.gempukku.minecraft.automation.lang;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ForTest extends ProgramTest {
    @Test
    public void forExecutesUntilConditionIsFalse() throws IllegalSyntaxException, IOException, ExecutionException {
        final Variable variable = executeScript("var sum = 0; for (var i=0; i<10; i=i+1) sum = sum + 1; return sum;");
        assertEquals(10, ((Number) variable.getValue()).intValue());
    }

    @Test
    public void initializationExecutedEvenIfConditionIsFalse() throws IllegalSyntaxException, IOException, ExecutionException {
        final Variable variable = executeScript("var v; for (v=\"Test\"; false; ) v=\"Failed\"; return v;");
        assertEquals("Test", variable.getValue());
    }

    @Test
    public void initializationIsOptional() throws IllegalSyntaxException, IOException, ExecutionException {
        final Variable variable = executeScript("var v; var i=0; for (;i<1; i=i+1) v=\"Test\"; return v;");
        assertEquals("Test", variable.getValue());
    }

    @Test
    public void afterLoopIsOptional() throws IllegalSyntaxException, IOException, ExecutionException {
        final Variable variable = executeScript("var v; for (var i=0;i<1; ) { v=\"Test\"; i=i+1;} return v;");
        assertEquals("Test", variable.getValue());
    }

    @Test
    public void inLoopIsOptional() throws IllegalSyntaxException, IOException, ExecutionException {
        final Variable variable = executeScript("var v; for (v=0;v<1; v=v+1); return v;");
        assertEquals(1, ((Number) variable.getValue()).intValue());
    }

    @Test
    public void variableDefinedInInitializationIsNotVisibleOutside() throws IllegalSyntaxException, IOException {
        try {
            final Variable variable = executeScript("for (var i=0; i<10; i=i+1); return i;");
            fail("Expected ExecutionException");
        } catch (ExecutionException exp) {
            // Expected
        }

    }
}
