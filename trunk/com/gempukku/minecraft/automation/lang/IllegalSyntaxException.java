package com.gempukku.minecraft.automation.lang;

import com.gempukku.minecraft.automation.lang.parser.Term;

public class IllegalSyntaxException extends Exception {
	private int _line;
	private int _column;

	public IllegalSyntaxException(String message) {
		this(0, 0, message);
	}

	public IllegalSyntaxException(Term term, String message) {
		this(term.getLine(), term.getColumn(), message);
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
