package com.gempukku.minecraft.automation.gui.computer.console;

import com.gempukku.minecraft.automation.Automation;
import com.gempukku.minecraft.automation.computer.ComputerConsole;
import com.gempukku.minecraft.automation.lang.IllegalSyntaxException;
import com.gempukku.minecraft.automation.lang.parser.ScriptParser;
import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.network.packet.Packet250CustomPayload;
import org.lwjgl.input.Keyboard;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class ProgramEditingConsoleGui {
	private static final int COMPILE_TICK_DELAY = 50;
	private static final int BLINK_LENGTH = 20;
	private static final int PROGRAM_TEXT_COLOR = 0xffffffff;
	private static final int PROGRAM_CURSOR_COLOR = 0xffff0000;
	private static final int PROGRAM_LAST_LINE_COLOR = 0xffff0000;

	private boolean _waitingForExitConfirmation = false;

	private boolean _programSaveDirty;
	private boolean _programCompileDirty;
	private int _ticksSinceLastModified;

	private IllegalSyntaxException _compileError;
	private boolean _compileSuccess;

	private String _editedProgramName;
	private List<StringBuilder> _editedProgramLines;
	private int _editedProgramCursorX;
	private int _editedProgramCursorY;
	private int _editedDisplayStartX;
	private int _editedDisplayStartY;

	private int _blinkDrawTick;

	private ComputerConsoleGui _computerConsoleGui;

	public ProgramEditingConsoleGui(ComputerConsoleGui computerConsoleGui) {
		_computerConsoleGui = computerConsoleGui;
	}

	public void drawEditProgramConsole(float timeSinceLastTick) {
		if (_ticksSinceLastModified > COMPILE_TICK_DELAY && _programCompileDirty) {
			compileProgram();
		}

		for (int line = _editedDisplayStartY; line < Math.min(_editedProgramLines.size(), _editedDisplayStartY + ComputerConsole.CONSOLE_HEIGHT - 1); line++) {
			String programLine = _editedProgramLines.get(line).toString();
			if (programLine.length() > _editedDisplayStartX) {
				String displayedLine = programLine.substring(_editedDisplayStartX, Math.min(programLine.length(), _editedDisplayStartX + ComputerConsole.CONSOLE_WIDTH));
				_computerConsoleGui.drawMonospacedLine(displayedLine, 0, (line - _editedDisplayStartY) * ComputerConsoleGui.FONT_HEIGHT, PROGRAM_TEXT_COLOR);
			}
		}

		final int lastLineY = ComputerConsoleGui.FONT_HEIGHT * (ComputerConsole.CONSOLE_HEIGHT - 1);
		if (_waitingForExitConfirmation) {
			_computerConsoleGui.drawMonospacedLine("File was not saved, exit? [Y]es/[N]o", 0, lastLineY, PROGRAM_LAST_LINE_COLOR);
		} else {
			_computerConsoleGui.drawMonospacedLine("[S]ave E[x]it", 0, lastLineY, PROGRAM_LAST_LINE_COLOR);
		}

		_blinkDrawTick = ((++_blinkDrawTick) % BLINK_LENGTH);
		if (_blinkDrawTick * 2 > BLINK_LENGTH)
			_computerConsoleGui.drawVerticalLine((_editedProgramCursorX - _editedDisplayStartX) * ComputerConsoleGui.CHARACTER_WIDTH - 1, 1 + (_editedProgramCursorY - _editedDisplayStartY) * ComputerConsoleGui.FONT_HEIGHT, (_editedProgramCursorY - _editedDisplayStartY + 1) * ComputerConsoleGui.FONT_HEIGHT, PROGRAM_CURSOR_COLOR);

		_ticksSinceLastModified++;
	}

	private void compileProgram() {
		ScriptParser parser = new ScriptParser();
		try {
			parser.parseScript(new StringReader(getProgramText()));
			_compileSuccess = true;
		} catch (IllegalSyntaxException exp) {
			_compileError = exp;
		} catch (IOException exp) {
			// Ignore, we are reading from String
		}
	}

	public void keyTypedInEditingProgram(char character, int keyboardCharId) {
		if (_waitingForExitConfirmation) {
			if (keyboardCharId == Keyboard.KEY_N)
				_waitingForExitConfirmation = false;
			else if (keyboardCharId == Keyboard.KEY_Y) {
				_waitingForExitConfirmation = false;
				_computerConsoleGui.exitProgramming();
			}
		} else {
			StringBuilder editedLine = _editedProgramLines.get(_editedProgramCursorY);
			if (character >= 32 && character < 127) {
				editedLine.insert(_editedProgramCursorX, character);
				_editedProgramCursorX++;
				setAllDirty();
			} else if (keyboardCharId == Keyboard.KEY_BACK) {
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
				setAllDirty();
			} else if (keyboardCharId == Keyboard.KEY_DELETE) {
				if (_editedProgramCursorX < editedLine.length()) {
					editedLine.delete(_editedProgramCursorX, _editedProgramCursorX + 1);
				} else if (_editedProgramCursorY < _editedProgramLines.size() - 1) {
					editedLine.append(_editedProgramLines.get(_editedProgramCursorY + 1));
					_editedProgramLines.remove(_editedProgramCursorY + 1);
				}
				setAllDirty();
			} else if (keyboardCharId == Keyboard.KEY_LEFT) {
				if (_editedProgramCursorX > 0) {
					_editedProgramCursorX--;
				} else if (_editedProgramCursorY > 0) {
					_editedProgramCursorX = _editedProgramLines.get(_editedProgramCursorY - 1).length();
					_editedProgramCursorY--;
				}
			} else if (keyboardCharId == Keyboard.KEY_RIGHT) {
				if (_editedProgramCursorX < editedLine.length()) {
					_editedProgramCursorX++;
				} else if (_editedProgramCursorY < _editedProgramLines.size() - 1) {
					_editedProgramCursorX = 0;
					_editedProgramCursorY++;
				}
			} else if (keyboardCharId == Keyboard.KEY_UP && _editedProgramCursorY > 0) {
				_editedProgramCursorY--;
			} else if (keyboardCharId == Keyboard.KEY_DOWN && _editedProgramCursorY < _editedProgramLines.size() - 1) {
				_editedProgramCursorY++;
			} else if (keyboardCharId == Keyboard.KEY_HOME) {
				_editedProgramCursorX = 0;
			} else if (keyboardCharId == Keyboard.KEY_END) {
				_editedProgramCursorX = editedLine.length();
			} else if (keyboardCharId == Keyboard.KEY_RETURN) {
				String remainingInLine = editedLine.substring(_editedProgramCursorX);
				editedLine.delete(_editedProgramCursorX, editedLine.length());
				_editedProgramLines.add(_editedProgramCursorY + 1, new StringBuilder(remainingInLine));
				_editedProgramCursorX = 0;
				_editedProgramCursorY++;
				setAllDirty();
			} else if (keyboardCharId == Keyboard.KEY_S && _computerConsoleGui.isCtrlKeyDown()) {
				String program = getProgramText();
				try {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					DataOutputStream os = new DataOutputStream(baos);
					os.writeUTF(_editedProgramName);
					os.writeUTF(program);
					PacketDispatcher.sendPacketToServer(new Packet250CustomPayload(Automation.SAVE_PROGRAM, baos.toByteArray()));
					setDirty(false, true);
				} catch (IOException exp) {
					// TODO
				}
			} else if (keyboardCharId == Keyboard.KEY_X && _computerConsoleGui.isCtrlKeyDown()) {
				if (!_programSaveDirty) {
					_computerConsoleGui.exitProgramming();
				} else {
					_waitingForExitConfirmation = true;
				}
			}

			// Adjust cursor X position to be within the program line
			if (_editedProgramCursorX > _editedProgramLines.get(_editedProgramCursorY).length())
				_editedProgramCursorX = _editedProgramLines.get(_editedProgramCursorY).length();
		}
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
	}

	public void setProgramText(String programText) {
		_editedProgramLines = new ArrayList<StringBuilder>();
		for (String line : programText.split("\n"))
			_editedProgramLines.add(new StringBuilder(line));

		_editedProgramCursorX = 0;
		_editedProgramCursorY = 0;
		setDirty(false, true);
	}

	private void setAllDirty() {
		_programSaveDirty = true;
		_programCompileDirty = true;
		_ticksSinceLastModified = 0;
	}

	private void setDirty(boolean saveDirty, boolean compileDirty) {
		_programSaveDirty = saveDirty;
		_programCompileDirty = compileDirty;
		if (compileDirty) {
			_compileSuccess = false;
			_compileError = null;
		}
		_ticksSinceLastModified = 0;
	}
}
