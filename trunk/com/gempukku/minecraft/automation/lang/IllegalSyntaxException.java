package com.gempukku.minecraft.automation.lang;

public class IllegalSyntaxException extends Exception {
	private int _line;
	private int _column;

	public IllegalSyntaxException(String message) {
		super(message);
	}

	public IllegalSyntaxException(int line, int column, String message) {
		super(message);
		_line = line;
		_column = column;
	}

	public int getColumn() {
		return _column;
	}

	public int getLine() {
		return _line;
	}
}
