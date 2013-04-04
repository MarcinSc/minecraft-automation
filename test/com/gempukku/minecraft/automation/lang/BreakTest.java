package com.gempukku.minecraft.automation.lang;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class BreakTest extends ProgramTest {
    @Test
    public void breakFor() throws IllegalSyntaxException, IOException, ExecutionException {
        final Variable variable = executeScript("var v; var i; for (i=0; i<10; i=i+1) { v = \"Test\"; break; v=\"Failed break\"; } if (i != 0) v=\"Failed i\"; return v;");
        assertEquals("Test", variable.getValue());
    }

    @Test
    public void breakElseIf() throws IllegalSyntaxException, IOException, ExecutionException {
        final Variable variable = executeScript("var v; var i=0; if (i==0) { i = 1; v=\"Test\"; } else if (i==1) { v=\"Failed else if\"; } else { v=\"Failed else\"; } return v;");
        assertEquals("Test", variable.getValue());
    }

    @Test
    public void breakWhile() throws IllegalSyntaxException, IOException, ExecutionException {
        final Variable variable = executeScript("var v; var i=0; while (i<2) { v=\"Test\"; i=i+1; break; } if (i != 1) v=\"Failed\"; return v;");
        assertEquals("Test", variable.getValue());
    }
}
