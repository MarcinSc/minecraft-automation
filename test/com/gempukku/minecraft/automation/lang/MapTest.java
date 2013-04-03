package com.gempukku.minecraft.automation.lang;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class MapTest extends ProgramTest {
    @Test
    public void defineMap() throws IllegalSyntaxException, IOException, ExecutionException {
        final Variable variable = executeScript("var map = { a: \"Test\", b: 1}; return map[\"a\"];");
        assertEquals("Test", variable.getValue());
    }

    @Test
    public void mapAssignValue() throws IllegalSyntaxException, IOException, ExecutionException {
        final Variable variable = executeScript("var map = {}; map[\"name\"] = \"Test\"; return map[\"name\"];");
        assertEquals("Test", variable.getValue());
    }
}
