package com.gempukku.minecraft.automation.lang;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class AssignTest extends ProgramTest {
	@Test
	public void defineAndAssign() throws IllegalSyntaxException, IOException, ExecutionException {
		final Variable result = executeScript("var v = \"Test\";return v;");
		assertEquals("Test", result.getValue());
	}


	@Test
	public void assignWhenNotDefined() throws IOException, ExecutionException {
		try {
			executeScript("v = \"Test\";return v;");
			fail("Expected IllegalSyntaxException");
		} catch (IllegalSyntaxException exp) {
			// Expected
		}
	}

	@Test
	public void accessWhenNoValueSet() throws IllegalSyntaxException, IOException, ExecutionException {
		final Variable value = executeScript("var v;\n return v;");
		assertEquals(Variable.Type.NULL, value.getType());
		assertNull(value.getValue());
	}
}
