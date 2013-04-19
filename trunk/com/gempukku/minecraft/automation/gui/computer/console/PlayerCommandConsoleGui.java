package com.gempukku.minecraft.automation.gui.computer.console;

import com.gempukku.minecraft.automation.computer.ComputerConsole;
import org.lwjgl.input.Keyboard;

public class PlayerCommandConsoleGui {
	public static final int PLAYER_CONSOLE_TEXT_COLOR = 0xffffffff;
	public static final int PLAYER_CONSOLE_CURSOR_COLOR = 0xffff0000;
	private static final int BLINK_LENGTH = 20;

	private ComputerConsole _playerConsole = new ComputerConsole();
	private StringBuilder _currentCommand = new StringBuilder();
	private int _cursorPositionInPlayerCommand = 0;
	private int _currentCommandDisplayStartIndex = 0;

	private int _blinkDrawTick;

	private ComputerConsoleGui _computerConsoleGui;

	public PlayerCommandConsoleGui(ComputerConsoleGui computerConsoleGui) {
		_computerConsoleGui = computerConsoleGui;
	}

	public void drawPlayerCommandConsole(float timeSinceLastTick) {
		final String[] consoleLines = _playerConsole.getLines();
		// Draw all lines but first (we need to fill current edited line at the bottom)
		for (int i = 1; i < consoleLines.length; i++)
			_computerConsoleGui.drawMonospacedText(consoleLines[i], 0, (i - 1) * ComputerConsoleGui.FONT_HEIGHT, PLAYER_CONSOLE_TEXT_COLOR);

		String wholeCommandLine = ">" + _currentCommand.toString();
		String commandLine = wholeCommandLine.substring(_currentCommandDisplayStartIndex, Math.min(_currentCommandDisplayStartIndex + ComputerConsole.CONSOLE_WIDTH, wholeCommandLine.length()));
		int cursorPositionInDisplayedCommandLine = 1 + _cursorPositionInPlayerCommand - _currentCommandDisplayStartIndex;

		final int lastLineY = ComputerConsoleGui.FONT_HEIGHT * (ComputerConsole.CONSOLE_HEIGHT - 1);
		_computerConsoleGui.drawMonospacedText(commandLine, 0, lastLineY, PLAYER_CONSOLE_TEXT_COLOR);

		_blinkDrawTick = ((++_blinkDrawTick) % BLINK_LENGTH);
		if (_blinkDrawTick * 2 > BLINK_LENGTH)
			_computerConsoleGui.drawVerticalLine(cursorPositionInDisplayedCommandLine * ComputerConsoleGui.CHARACTER_WIDTH - 1, 1 + lastLineY, lastLineY + ComputerConsoleGui.FONT_HEIGHT, PLAYER_CONSOLE_CURSOR_COLOR);
	}

	public void keyTypedInPlayerConsole(char character, int keyboardCharId) {
		if (character >= 32 && character < 127) {
			_currentCommand.insert(_cursorPositionInPlayerCommand, character);
			_cursorPositionInPlayerCommand++;
		} else if (keyboardCharId == Keyboard.KEY_BACK && _cursorPositionInPlayerCommand > 0) {
			_currentCommand.delete(_cursorPositionInPlayerCommand - 1, _cursorPositionInPlayerCommand);
			_cursorPositionInPlayerCommand--;
		} else if (keyboardCharId == Keyboard.KEY_DELETE && _cursorPositionInPlayerCommand < _currentCommand.length()) {
			_currentCommand.delete(_cursorPositionInPlayerCommand, _cursorPositionInPlayerCommand + 1);
		} else if (keyboardCharId == Keyboard.KEY_LEFT && _cursorPositionInPlayerCommand > 0) {
			_cursorPositionInPlayerCommand--;
		} else if (keyboardCharId == Keyboard.KEY_RIGHT && _cursorPositionInPlayerCommand < _currentCommand.length()) {
			_cursorPositionInPlayerCommand++;
		} else if (keyboardCharId == Keyboard.KEY_HOME) {
			_cursorPositionInPlayerCommand = 0;
		} else if (keyboardCharId == Keyboard.KEY_END) {
			_cursorPositionInPlayerCommand = _currentCommand.length();
		} else if (keyboardCharId == Keyboard.KEY_RETURN) {
			String command = _currentCommand.toString().trim();
			_playerConsole.appendString(">" + command);

			_computerConsoleGui.executeCommand(command);
			_currentCommand = new StringBuilder();
			_cursorPositionInPlayerCommand = 0;
		}

		// Adjust start position
		int lineLength = _currentCommand.length() + 1;
		if (lineLength <= ComputerConsole.CONSOLE_WIDTH)
			_currentCommandDisplayStartIndex = 0;
		else {
			int cursorPositionInCommand = 1 + _cursorPositionInPlayerCommand;
			if (_currentCommandDisplayStartIndex + ComputerConsole.CONSOLE_WIDTH > lineLength)
				_currentCommandDisplayStartIndex = lineLength - ComputerConsole.CONSOLE_WIDTH;
			else if (cursorPositionInCommand > ComputerConsole.CONSOLE_WIDTH + _currentCommandDisplayStartIndex)
				_currentCommandDisplayStartIndex = cursorPositionInCommand - ComputerConsole.CONSOLE_WIDTH;
			else if (cursorPositionInCommand - 1 < _currentCommandDisplayStartIndex)
				_currentCommandDisplayStartIndex = cursorPositionInCommand - 1;
		}
	}

	public void appendToConsole(String text) {
		_playerConsole.appendString(text);
	}
}
