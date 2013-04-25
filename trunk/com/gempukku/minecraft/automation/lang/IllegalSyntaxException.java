package com.gempukku.minecraft.automation.lang;

import com.gempukku.minecraft.automation.lang.parser.LastPeekingIterator;
import com.gempukku.minecraft.automation.lang.parser.Term;
import com.gempukku.minecraft.automation.lang.parser.TermBlock;

public class IllegalSyntaxException extends Exception {
	private int _line;
	private int _column;

	public IllegalSyntaxException(LastPeekingIterator<TermBlock> termIterator, String message) {
		super(message);
		if (termIterator.hasNext()) {
			final TermBlock termBlock = termIterator.peek();
			if (termBlock.isTerm()) {
				final Term term = termBlock.getTerm();
				_line = term.getLine();
				_column = term.getColumn();
			} else {
				_line = termBlock.getBlockStartLine();
				_column = termBlock.getBlockStartColumn();
			}
		} else {
			final TermBlock lastTermBlock = termIterator.getLast();
			if (lastTermBlock.isTerm()) {
				final Term lastTerm = lastTermBlock.getTerm();
				_line = lastTerm.getLine();
				_column = lastTerm.getColumn() + lastTerm.getValue().length();
			} else {
				_line = lastTermBlock.getBlockEndLine();
				_column = lastTermBlock.getBlockEndColumn();
			}
		}
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
