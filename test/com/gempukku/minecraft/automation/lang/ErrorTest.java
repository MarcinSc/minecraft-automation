package com.gempukku.minecraft.automation.lang;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.fail;

public class ErrorTest extends ProgramTest {
	@Test
	public void errorTest() throws IllegalSyntaxException, IOException, ExecutionException {
		try {
			executeScript("  \nfoo bar");
			fail("Expected IllegalSyntaxException");
		} catch (IllegalSyntaxException exp) {
			// expected
		}
	}
}
