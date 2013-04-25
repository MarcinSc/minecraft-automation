package com.gempukku.minecraft.automation.lang;

public class ExecutionException extends Exception {
	private int _line = -1;

	public ExecutionException(String message) {
		super(message);
	}

	public ExecutionException(int line, String message) {
		super(message);
		_line = line;
	}

	public int getLine() {
		return _line;
	}
}
