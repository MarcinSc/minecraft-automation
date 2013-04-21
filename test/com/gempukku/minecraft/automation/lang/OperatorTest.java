package com.gempukku.minecraft.automation.lang;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class OperatorTest extends ProgramTest {
	@Test
	public void orOperator() throws IllegalSyntaxException, IOException, ExecutionException {
		assertEquals("Test", executeScript("if (true || false) return \"Test\";").getValue());
		assertEquals("Test", executeScript("if (false || false) return \"Failed\"; else return \"Test\";").getValue());
		assertEquals("Test", executeScript("if (true || true) return \"Test\";").getValue());
		assertEquals("Test", executeScript("if (false || true) return \"Test\";").getValue());
	}

	@Test
	public void andOperator() throws IllegalSyntaxException, IOException, ExecutionException {
		assertEquals("Test", executeScript("  if (true && false) return \"Failed\"; else return \"Test\";").getValue());
		assertEquals("Test", executeScript("if (false && false) return \"Failed\"; else return \"Test\";").getValue());
		assertEquals("Test", executeScript(" if (true && true) return \"Test\";").getValue());
		assertEquals("Test", executeScript(" if (false && true) return \"Failed\"  ;    else return \"Test\";").getValue());
	}

	@Test
	public void notOperator() throws IllegalSyntaxException, IOException, ExecutionException {
		assertEquals("Test", executeScript("if (!false) return \"Test\";").getValue());
		assertEquals("Test", executeScript("if (!true) return \"Failed\"; else return \"Test\";").getValue());
		assertEquals("Test", executeScript("function func() { return false; } if (!func()) return \"Test\";").getValue());
	}
}
