package com.gempukku.minecraft.automation.lang;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ListTest extends ProgramTest {
	@Test
	public void defineListGetElement() throws IllegalSyntaxException, IOException, ExecutionException {
		final Variable variable = executeScript("var list = [\"Test\", 12]; return list[0];");
		assertEquals("Test", variable.getValue());
	}

	@Test
	public void defineListGetSize() throws IllegalSyntaxException, IOException, ExecutionException {
		final Variable variable = executeScript("var list = [\"Test\", 12]; return list.getSize();");
		assertEquals(2, variable.getValue());
	}

	@Test
	public void defineListAddElement() throws IllegalSyntaxException, IOException, ExecutionException {
		final Variable variable = executeScript("var list = []; list.add(\"Test\"); return list[0];");
		assertEquals("Test", variable.getValue());
	}

	@Test
	public void defineListRemoveElement() throws IllegalSyntaxException, IOException, ExecutionException {
		final Variable variable = executeScript("var list = [12, \"Test\"]; list.remove(0); return list[0];");
		assertEquals("Test", variable.getValue());
	}
}
