package com.gempukku.minecraft.automation.gui.computer.console;

import com.gempukku.minecraft.automation.Automation;
import com.gempukku.minecraft.automation.computer.ComputerConsole;
import com.gempukku.minecraft.automation.lang.IllegalSyntaxException;
import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.packet.Packet250CustomPayload;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProgramEditingConsoleGui {
	private static final int BLINK_LENGTH = 20;
	private static final int PROGRAM_TEXT_COLOR = 0xffffffff;
	private static final int PROGRAM_CURSOR_COLOR = 0xff0000ff;
	private static final int PROGRAM_ERROR_UNDERLINE_COLOR = 0xffff0000;
	private static final int PROGRAM_LAST_LINE_COLOR = 0xffff0000;
	private static final int PROGRAM_ERROR_MESSAGE_COLOR = 0xffff0000;

	private static final int COMPILE_PENDING_COLOR = 0xffffff00;
	private static final int COMPILE_ERROR_COLOR = 0xffff0000;
	private static final int COMPILE_OK_COLOR = 0xff00ff00;

	private boolean _waitingForExitConfirmation = false;
	private boolean _waitingForGotoLineEntered = false;
	private boolean _displayErrorMessage = false;

	private boolean _programSaveDirty;
	private boolean _programCompileDirty;

	private String _editedProgramName;
	private List<StringBuilder> _editedProgramLines;
	private int _editedProgramCursorX;
	private int _editedProgramCursorY;
	private int _editedDisplayStartX;
	private int _editedDisplayStartY;

	private int _blinkDrawTick;

	private ComputerConsoleGui _computerConsoleGui;
	private CompileScriptOnTheFly _onTheFlyCompiler;
	private StringBuilder _gotoLineNumber;

	private int _scale = 3;
	private static final float[] SCALES = new float[]{1f, 0.833f, 0.667f, 0.5f};
	private static final int[] CHARACTER_COUNT_WIDTH = new int[]{ComputerConsole.CONSOLE_WIDTH, (int) (ComputerConsole.CONSOLE_WIDTH * 1.2f), (int) (ComputerConsole.CONSOLE_WIDTH * 1.5f), ComputerConsole.CONSOLE_WIDTH * 2};
	private static final int[] CHARACTER_COUNT_HEIGHT = new int[]{ComputerConsole.CONSOLE_HEIGHT, (int) (ComputerConsole.CONSOLE_HEIGHT * 1.2f), (int) (ComputerConsole.CONSOLE_HEIGHT * 1.5f), ComputerConsole.CONSOLE_HEIGHT * 2};

	public ProgramEditingConsoleGui(ComputerConsoleGui computerConsoleGui) {
		_computerConsoleGui = computerConsoleGui;
		_onTheFlyCompiler = new CompileScriptOnTheFly();
		_onTheFlyCompiler.startCompiler();
	}

	public void drawEditProgramConsole(float timeSinceLastTick, int width, int height) {
		GL11.glPushMatrix();
		try {
			GL11.glScalef(SCALES[_scale], SCALES[_scale], SCALES[_scale]);
			for (int line = _editedDisplayStartY; line < Math.min(_editedProgramLines.size(), _editedDisplayStartY + getCharactersInColumn() - 1); line++) {
				String programLine = _editedProgramLines.get(line).toString();
				if (programLine.length() > _editedDisplayStartX) {
					String displayedLine = programLine.substring(_editedDisplayStartX, Math.min(programLine.length(), _editedDisplayStartX + getCharactersInRow()));
					_computerConsoleGui.drawMonospacedText(displayedLine, 0, (line - _editedDisplayStartY) * ComputerConsoleGui.FONT_HEIGHT, PROGRAM_TEXT_COLOR);
				}
			}

			// Draw status line
			drawStatusLine();

			_blinkDrawTick = ((++_blinkDrawTick) % BLINK_LENGTH);
			if (_blinkDrawTick * 2 > BLINK_LENGTH)
				_computerConsoleGui.drawVerticalLine((_editedProgramCursorX - _editedDisplayStartX) * ComputerConsoleGui.CHARACTER_WIDTH - 1, (_editedProgramCursorY - _editedDisplayStartY) * ComputerConsoleGui.FONT_HEIGHT, 1 + (_editedProgramCursorY - _editedDisplayStartY + 1) * ComputerConsoleGui.FONT_HEIGHT, PROGRAM_CURSOR_COLOR);
		} finally {
			GL11.glPopMatrix();
		}
	}

	private int getCharactersInColumn() {
		return CHARACTER_COUNT_HEIGHT[_scale];
	}

	private int getCharactersInRow() {
		return CHARACTER_COUNT_WIDTH[_scale];
	}

	private void drawStatusLine() {
		final int lastLineY = ComputerConsoleGui.FONT_HEIGHT * (getCharactersInColumn() - 1);
		final CompileScriptOnTheFly.CompileStatus compileStatusObj = _onTheFlyCompiler.getCompileStatus();
		if (_waitingForExitConfirmation) {
			_computerConsoleGui.drawMonospacedText("File was not saved, exit? [Y]es/[N]o", 0, lastLineY, PROGRAM_LAST_LINE_COLOR);
		} else if (_waitingForGotoLineEntered) {
			_computerConsoleGui.drawMonospacedText("Go to line: " + _gotoLineNumber.toString(), 0, lastLineY, PROGRAM_LAST_LINE_COLOR);
		} else if (_displayErrorMessage && compileStatusObj != null && compileStatusObj.error != null) {
			displayErrorInformation(lastLineY, compileStatusObj);
		} else {
			displayNormalEditingInformation(lastLineY, compileStatusObj);
		}
	}

	private void displayNormalEditingInformation(int lastLineY, CompileScriptOnTheFly.CompileStatus compileStatusObj) {
		_computerConsoleGui.drawMonospacedText("[S]ave E[x]it", 0, lastLineY, PROGRAM_LAST_LINE_COLOR);

		if (_programSaveDirty)
			_computerConsoleGui.drawMonospacedText("*", 15 * ComputerConsoleGui.CHARACTER_WIDTH, lastLineY, PROGRAM_LAST_LINE_COLOR);

		String compileStatus = "...";
		int compileColor = COMPILE_PENDING_COLOR;
		if (compileStatusObj != null) {
			if (compileStatusObj.success) {
				compileStatus = "OK";
				compileColor = COMPILE_OK_COLOR;
			} else if (compileStatusObj.error != null) {
				compileStatus = "[E]rror";
				compileColor = COMPILE_ERROR_COLOR;
			} else {
				compileStatus = "Unknown error";
				compileColor = COMPILE_ERROR_COLOR;
			}
		}

		int index = getCharactersInRow() - compileStatus.length();
		_computerConsoleGui.drawMonospacedText(compileStatus, index * ComputerConsoleGui.CHARACTER_WIDTH, lastLineY, compileColor);

		if (compileStatusObj != null && compileStatusObj.error != null) {
			final IllegalSyntaxException error = compileStatusObj.error;
			final int errorLine = error.getLine() - _editedDisplayStartY;
			final int errorColumn = error.getColumn() - _editedDisplayStartX;

			if (errorLine >= 0 && errorLine < getCharactersInColumn() - 1
							&& errorColumn >= 0 && errorColumn < getCharactersInRow())
				_computerConsoleGui.drawHorizontalLine(errorColumn * ComputerConsoleGui.CHARACTER_WIDTH, (errorColumn + 1) * ComputerConsoleGui.CHARACTER_WIDTH, (errorLine + 1) * ComputerConsoleGui.FONT_HEIGHT, PROGRAM_ERROR_UNDERLINE_COLOR);
		}
	}

	private void displayErrorInformation(int lastLineY, CompileScriptOnTheFly.CompileStatus compileStatusObj) {
		final IllegalSyntaxException error = compileStatusObj.error;
		_computerConsoleGui.drawMonospacedText(error.getMessage(), 0, lastLineY, PROGRAM_ERROR_MESSAGE_COLOR);
		final int errorLine = error.getLine() - _editedDisplayStartY;
		final int errorColumn = error.getColumn() - _editedDisplayStartX;

		if (errorLine >= 0 && errorLine < getCharactersInColumn() - 1
						&& errorColumn >= 0 && errorColumn < getCharactersInRow())
			_computerConsoleGui.drawHorizontalLine(errorColumn * ComputerConsoleGui.CHARACTER_WIDTH, (errorColumn + 1) * ComputerConsoleGui.CHARACTER_WIDTH, (errorLine + 1) * ComputerConsoleGui.FONT_HEIGHT, PROGRAM_ERROR_UNDERLINE_COLOR);
	}

	public void keyTypedInEditingProgram(char character, int keyboardCharId) {
		if (_waitingForExitConfirmation) {
			if (keyboardCharId == Keyboard.KEY_N)
				_waitingForExitConfirmation = false;
			else if (keyboardCharId == Keyboard.KEY_Y) {
				_waitingForExitConfirmation = false;
				_computerConsoleGui.exitProgramming();
			}
		} else if (_waitingForGotoLineEntered) {
			if (character >= 32 && character < 127 && Character.isDigit(character) && _gotoLineNumber.length() < 5) {
				_gotoLineNumber.append(character);
			} else if (keyboardCharId == Keyboard.KEY_ESCAPE) {
				_waitingForGotoLineEntered = false;
			} else if (keyboardCharId == Keyboard.KEY_BACK && _gotoLineNumber.length() > 1) {
				_gotoLineNumber.delete(_gotoLineNumber.length() - 1, _gotoLineNumber.length());
			} else if (keyboardCharId == Keyboard.KEY_RETURN) {
				if (_gotoLineNumber.length() > 0) {
					_editedProgramCursorX = 0;
					_editedProgramCursorY = Math.min(Integer.parseInt(_gotoLineNumber.toString()), _editedProgramLines.size() - 1);
				}
				_waitingForGotoLineEntered = false;
			}
		} else {
			StringBuilder editedLine = _editedProgramLines.get(_editedProgramCursorY);
			if (character >= 32 && character < 127) {
				editedLine.insert(_editedProgramCursorX, character);
				_editedProgramCursorX++;
				programModified();
			} else if (keyboardCharId == Keyboard.KEY_BACK) {
				handleBackspace(editedLine);
			} else if (keyboardCharId == Keyboard.KEY_DELETE) {
				handleDelete(editedLine);
			} else if (keyboardCharId == Keyboard.KEY_LEFT) {
				handleLeft();
			} else if (keyboardCharId == Keyboard.KEY_RIGHT) {
				handleRight(editedLine);
			} else if (keyboardCharId == Keyboard.KEY_UP && _computerConsoleGui.isCtrlKeyDown()) {
				handleScaleUp();
			} else if (keyboardCharId == Keyboard.KEY_DOWN && _computerConsoleGui.isCtrlKeyDown()) {
				handleScaleDown();
			} else if (keyboardCharId == Keyboard.KEY_UP && _editedProgramCursorY > 0) {
				handleUp();
			} else if (keyboardCharId == Keyboard.KEY_DOWN && _editedProgramCursorY < _editedProgramLines.size() - 1) {
				handleDown();
			} else if (keyboardCharId == Keyboard.KEY_HOME) {
				handleHome();
			} else if (keyboardCharId == Keyboard.KEY_END) {
				handleEnd(editedLine);
			} else if (keyboardCharId == Keyboard.KEY_RETURN) {
				handleEnter(editedLine);
			} else if (keyboardCharId == Keyboard.KEY_S && _computerConsoleGui.isCtrlKeyDown()) {
				handleSave();
			} else if (keyboardCharId == Keyboard.KEY_X && _computerConsoleGui.isCtrlKeyDown()) {
				handleExit();
			} else if (keyboardCharId == Keyboard.KEY_E && _computerConsoleGui.isCtrlKeyDown()) {
				handleDisplayError();
			} else if (keyboardCharId == Keyboard.KEY_G && _computerConsoleGui.isCtrlKeyDown()) {
				handleGotoLine();
			} else if (keyboardCharId == Keyboard.KEY_V && _computerConsoleGui.isCtrlKeyDown()) {
				handlePaste();
			}
		}

		// Adjust cursor X position to be within the program line
		if (_editedProgramCursorX > _editedProgramLines.get(_editedProgramCursorY).length())
			_editedProgramCursorX = _editedProgramLines.get(_editedProgramCursorY).length();

		final int editedLineLength = _editedProgramLines.get(_editedProgramCursorY).length();
		if (_editedDisplayStartX + getCharactersInRow() > editedLineLength) {
			_editedDisplayStartX = Math.max(0, editedLineLength - getCharactersInRow());
		} else if (_editedProgramCursorX > _editedDisplayStartX + getCharactersInRow()) {
			_editedDisplayStartX = _editedProgramCursorX - getCharactersInRow();
		} else if (_editedProgramCursorX < _editedDisplayStartX) {
			_editedDisplayStartX = _editedProgramCursorX;
		}

		final int linesCount = _editedProgramLines.size();
		if (_editedDisplayStartY + getCharactersInColumn() - 1 > linesCount) {
			_editedDisplayStartY = Math.max(0, linesCount - getCharactersInColumn() + 1);
		} else if (_editedProgramCursorY > _editedDisplayStartY + getCharactersInColumn() - 2) {
			_editedDisplayStartY = _editedProgramCursorY - getCharactersInColumn() + 2;
		} else if (_editedProgramCursorY < _editedDisplayStartY) {
			_editedDisplayStartY = _editedProgramCursorY;
		}

		if (_programCompileDirty) {
			_onTheFlyCompiler.submitCompileRequest(getProgramText());
			_programCompileDirty = false;
		}
	}

	private void handleScaleDown() {
		if (_scale > 0)
			_scale--;
	}

	private void handleScaleUp() {
		if (_scale < SCALES.length - 1)
			_scale++;
	}

	private void handlePaste() {
		final String clipboard = GuiScreen.getClipboardString();
		final String[] lines = clipboard.split("\n");
		for (int index = 0; index < lines.length; index++) {
			String line = lines[index];
			final String fixedLine = ComputerConsole.stripInvalidCharacters(line);
			final StringBuilder currentLine = _editedProgramLines.get(_editedProgramCursorY);
			String before = currentLine.substring(0, _editedProgramCursorX);
			String after = currentLine.substring(_editedProgramCursorX);
			if (index < lines.length - 1) {
				_editedProgramLines.set(_editedProgramCursorY, new StringBuilder(before + fixedLine));
				_editedProgramLines.add(_editedProgramCursorY + 1, new StringBuilder(after));
				_editedProgramCursorY++;
				_editedProgramCursorX = 0;
			} else {
				// Last line
				_editedProgramLines.set(_editedProgramCursorY, new StringBuilder(before + fixedLine + after));
				_editedProgramCursorX = (before + fixedLine).length();
			}
		}
		if (clipboard.length() > 0)
			programModified();
	}

	private void handleDisplayError() {
		final CompileScriptOnTheFly.CompileStatus compileStatus = _onTheFlyCompiler.getCompileStatus();
		if (compileStatus != null && compileStatus.error != null) {
			final IllegalSyntaxException error = compileStatus.error;
			_editedProgramCursorY = error.getLine();
			_editedProgramCursorX = error.getColumn();
			_displayErrorMessage = true;
		}
	}

	private void handleExit() {
		if (!_programSaveDirty) {
			_onTheFlyCompiler.finishedEditing();
			_computerConsoleGui.exitProgramming();
		} else {
			_waitingForExitConfirmation = true;
		}
	}

	private void handleSave() {
		String program = getProgramText();
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream os = new DataOutputStream(baos);
			os.writeUTF(_editedProgramName);
			os.writeUTF(program);
			PacketDispatcher.sendPacketToServer(new Packet250CustomPayload(Automation.SAVE_PROGRAM, baos.toByteArray()));
			_programSaveDirty = false;
		} catch (IOException exp) {
			// TODO
		}
	}

	private void handleGotoLine() {
		_gotoLineNumber = new StringBuilder();
		_waitingForGotoLineEntered = true;
	}

	private void handleEnter(StringBuilder editedLine) {
		String remainingInLine = editedLine.substring(_editedProgramCursorX);
		editedLine.delete(_editedProgramCursorX, editedLine.length());
		int spaceCount = getSpaceCount(editedLine.toString());
		char[] spacesPrefix = new char[spaceCount];
		Arrays.fill(spacesPrefix, ' ');
		_editedProgramLines.add(_editedProgramCursorY + 1, new StringBuilder(new String(spacesPrefix) + remainingInLine));
		_editedProgramCursorX = spaceCount;
		_editedProgramCursorY++;
		programModified();
	}

	private int getSpaceCount(String s) {
		final char[] chars = s.toCharArray();
		for (int i = 0; i < chars.length; i++)
			if (chars[i] != ' ')
				return i;
		return chars.length;
	}

	private void handleEnd(StringBuilder editedLine) {
		_editedProgramCursorX = editedLine.length();
	}

	private void handleHome() {
		_editedProgramCursorX = 0;
	}

	private void handleDown() {
		_editedProgramCursorY++;
	}

	private void handleUp() {
		_editedProgramCursorY--;
	}

	private void handleRight(StringBuilder editedLine) {
		if (_editedProgramCursorX < editedLine.length()) {
			_editedProgramCursorX++;
		} else if (_editedProgramCursorY < _editedProgramLines.size() - 1) {
			_editedProgramCursorX = 0;
			_editedProgramCursorY++;
		}
	}

	private void handleLeft() {
		if (_editedProgramCursorX > 0) {
			_editedProgramCursorX--;
		} else if (_editedProgramCursorY > 0) {
			_editedProgramCursorX = _editedProgramLines.get(_editedProgramCursorY - 1).length();
			_editedProgramCursorY--;
		}
	}

	private void handleDelete(StringBuilder editedLine) {
		if (_editedProgramCursorX < editedLine.length()) {
			editedLine.delete(_editedProgramCursorX, _editedProgramCursorX + 1);
		} else if (_editedProgramCursorY < _editedProgramLines.size() - 1) {
			editedLine.append(_editedProgramLines.get(_editedProgramCursorY + 1));
			_editedProgramLines.remove(_editedProgramCursorY + 1);
		}
		programModified();
	}

	private void handleBackspace(StringBuilder editedLine) {
		if (_editedProgramCursorX > 0) {
			editedLine.delete(_editedProgramCursorX - 1, _editedProgramCursorX);
			_editedProgramCursorX--;
		} else if (_editedProgramCursorY > 0) {
			StringBuilder previousLine = _editedProgramLines.get(_editedProgramCursorY - 1);
			_editedProgramCursorX = previousLine.length();
			previousLine.append(editedLine);
			_editedProgramLines.remove(_editedProgramCursorY);
			_editedProgramCursorY--;
		}
		programModified();
	}

	private String getProgramText() {
		StringBuilder program = new StringBuilder();
		for (int i = 0; i < _editedProgramLines.size(); i++) {
			if (i > 0)
				program.append("\n");
			program.append(_editedProgramLines.get(i));
		}
		return program.toString();
	}

	public void reset(String programName) {
		_editedProgramName = programName;
		_editedProgramLines = new ArrayList<StringBuilder>();
		_editedProgramLines.add(new StringBuilder());
		_editedProgramCursorX = 0;
		_editedProgramCursorY = 0;
		_editedDisplayStartX = 0;
		_editedDisplayStartY = 0;
	}

	public void setProgramText(String programText) {
		_editedProgramLines = new ArrayList<StringBuilder>();
		for (String line : programText.split("\n"))
			_editedProgramLines.add(new StringBuilder(line));

		_editedProgramCursorX = 0;
		_editedProgramCursorY = 0;
		_editedDisplayStartX = 0;
		_editedDisplayStartY = 0;
		_programSaveDirty = false;
		_onTheFlyCompiler.submitCompileRequest(getProgramText());
	}

	private void programModified() {
		_programSaveDirty = true;
		_programCompileDirty = true;
		_displayErrorMessage = false;
	}
}
