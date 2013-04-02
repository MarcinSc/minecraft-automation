package com.gempukku.minecraft.automation.lang;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class IfTest extends ProgramTest {
    @Test
    public void ifExecuted() throws IllegalSyntaxException, IOException, ExecutionException {
        final Variable result = executeScript("var v; if (true) v = \"Test\"; return v;");
        assertEquals("Test", result.getValue());
    }

    @Test
    public void ifNotExecuted() throws IllegalSyntaxException, IOException, ExecutionException {
        final Variable result = executeScript("var v = \"Test\"; if (false) v = \"Failed\"; return v;");
        assertEquals("Test", result.getValue());
    }

    @Test
    public void ifWithBlock() throws IllegalSyntaxException, IOException, ExecutionException {
        final Variable result = executeScript("var v=\"Failed\"; if (true) { v = \"Test\"; } return v;");
        assertEquals("Test", result.getValue());
    }

    @Test
    public void ifBlockHasItsOwnScope() throws IllegalSyntaxException, IOException, ExecutionException {
        final Variable result = executeScript("var v=\"Test\"; if (true) { var v = \"Failed\"; } return v;");
        assertEquals("Test", result.getValue());
    }

    @Test
    public void variablesDefinedInIfBlockAreNotVisibleOutside() throws IllegalSyntaxException, IOException {
        try {
            executeScript("var v=\"Test\"; if (true) { var v2; } return v2;");
            fail("Expected ExecutionException");
        } catch (ExecutionException exp) {
            // Expected
        }
    }

    @Test
    public void ifWithInvalidType() throws IllegalSyntaxException, IOException {
        try {
            executeScript("if (12) return \"Test\";");
            fail("Expected ExecutionException");
        } catch (ExecutionException exp) {
            // Expected
        }
    }
}
