package com.gempukku.minecraft.automation.lang;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class WhileTest extends ProgramTest {
    @Test
    public void whileIsExecuted() throws IllegalSyntaxException, IOException, ExecutionException {
        final Variable variable = executeScript("var i=0; while (i<10) i=i+1; return i;");
        assertEquals(10, ((Number) variable.getValue()).intValue());
    }
    
    @Test
    public void whileIsNotExecutedIfConditionIsFalse() throws IllegalSyntaxException, IOException, ExecutionException {
        final Variable variable = executeScript("var v = \"Test\"; while (false) v = \"Failed\"; return v;");
        assertEquals("Test", variable.getValue());
    }
}
