package com.gempukku.minecraft.automation.gui.computer.console;

import com.gempukku.minecraft.automation.Automation;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.ComputerConsole;
import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ComputerConsoleGui extends GuiScreen {
  public static final int BACKGROUND_COLOR = 0xff111111;
  public static final int FRAME_COLOR = 0xffffffff;
  public static final int BUTTON_TEXT_COLOR = 0xffffffff;
  public static final int BUTTON_BG_HOVER_COLOR = 0xff7f7f7f;
  public static final int BUTTON_BG_ACTIVE_COLOR = 0xffbfbfbf;
  public static final int BUTTON_BG_INACTIVE_COLOR = 0xff3f3f3f;

  public static final int PLAYER_CONSOLE_TEXT_COLOR = 0xffffffff;
  public static final int PLAYER_CONSOLE_CURSOR_COLOR = 0xffff0000;
  public static final int COMPUTER_CONSOLE_TEXT_COLOR = 0xffffffff;

  public static final int PROGRAM_TEXT_COLOR = 0xffffffff;
  public static final int PROGRAM_CURSOR_COLOR = 0xffff0000;
  public static final int PROGRAM_LAST_LINE_COLOR = 0xffff0000;

  private static final int PADDING_HOR = 5;
  private static final int PADDING_VER = 5;
  private static final int BUTTON_PADDING_HOR = 3;
  private static final int BUTTON_PADDING_VER = 3;

  private static final int FONT_HEIGHT = 9;
  private static final int BLINK_LENGTH = 20;
  public static final String PLAYER_CONSOLE_TEXT = "Player console";
  public static final String COMPUTER_CONSOLE_TEXT = "Computer console";

  // mode=0 is user (player) console, mode=1 is program output console
  private int _mode = 0;

  private boolean _editingProgram;

  private String _editedProgramName;
  private List<StringBuilder> _editedProgramLines;
  private int _editedProgramCursorX;
  private int _editedProgramCursorY;
  private int _editedDisplayStartX;
  private int _editedDisplayStartY;

  private int _screenX;
  private int _screenY;
  private int _screenWidth;
  private int _screenHeight;

  private int _computerScreenWidth;
  private int _computerScreenHeight;

  private StringBuilder _currentCommand;
  private int _cursorPositionInPlayerCommand = 0;
  private int _currentCommandDisplayStartIndex = 0;

  private ComputerConsole _playerConsole = new ComputerConsole();
  private ComputerConsole _computerConsole = new ComputerConsole();
  private EntityPlayer _player;
  private ComputerTileEntity _tileEntity;
  private int _characterWidth;

  private RenderEngine _renderEngine;

  private int _blinkTick;

  private Rectangle _playerConsoleModeButton;
  private Rectangle _computerConsoleModeButton;

  public ComputerConsoleGui(EntityPlayer player, ComputerTileEntity tileEntity) {
    _player = player;
    _tileEntity = tileEntity;
    _renderEngine = Minecraft.getMinecraft().renderEngine;
    _playerConsole.appendString("AutomationOS v. 0.0");
    _playerConsole.appendString("You're logged in as " + _player.getEntityName() + ", use \"exit\" command to exit the console, use \"help\" to list commands.");
    _currentCommand = new StringBuilder();
  }

  @Override
  public boolean doesGuiPauseGame() {
    return false;
  }

  @Override
  public void initGui() {
    _characterWidth = 6;
    _computerScreenWidth = _characterWidth * ComputerConsole.CONSOLE_WIDTH;
    _computerScreenHeight = FONT_HEIGHT * ComputerConsole.CONSOLE_HEIGHT;
    Keyboard.enableRepeatEvents(true);
  }

  @Override
  public void onGuiClosed() {
    Keyboard.enableRepeatEvents(false);
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float timeSinceLastTick) {
    // Draw the overlay on the world view (gradient)
    drawDefaultBackground();

    _screenWidth = _computerScreenWidth + PADDING_HOR * 2;
    _screenHeight = _computerScreenHeight + PADDING_VER * 2 + BUTTON_PADDING_VER * 2 + FONT_HEIGHT;

    _screenX = (width - _screenWidth) / 2;
    _screenY = (height - _screenHeight) / 2;

    GL11.glPushMatrix();
    try {
      GL11.glTranslatef(_screenX, _screenY, 0);
      drawScreenInLocalCoords(mouseX - _screenX, mouseY - _screenY, timeSinceLastTick);
    } finally {
      GL11.glPopMatrix();
    }
  }

  private void drawScreenInLocalCoords(int mouseX, int mouseY, float timeSinceLastTick) {
    // Fill background with solid dark-grey
    drawRect(0, 0, _screenWidth, _screenHeight, BACKGROUND_COLOR);

    // Draw white rectangle around the screen
    drawHorizontalLine(0, _screenWidth, 0, FRAME_COLOR);
    drawHorizontalLine(0, _screenWidth, _screenHeight, FRAME_COLOR);
    drawVerticalLine(0, 0, _screenHeight, FRAME_COLOR);
    drawVerticalLine(_screenWidth, 0, _screenHeight, FRAME_COLOR);

    int buttonHeight = BUTTON_PADDING_VER * 2 + FONT_HEIGHT;

    int playerConsoleButtonWidth = fontRenderer.getStringWidth(PLAYER_CONSOLE_TEXT) + BUTTON_PADDING_HOR * 2;
    int computerConsoleButtonWidth = fontRenderer.getStringWidth(COMPUTER_CONSOLE_TEXT) + BUTTON_PADDING_HOR * 2;

    boolean playerConsoleHover = mouseX >= PADDING_HOR && mouseX < PADDING_HOR + playerConsoleButtonWidth
            && mouseY >= PADDING_VER && mouseY < PADDING_VER + buttonHeight;
    boolean computerConsoleHover = mouseX >= PADDING_HOR + playerConsoleButtonWidth && mouseX < PADDING_HOR + playerConsoleButtonWidth + computerConsoleButtonWidth
            && mouseY >= PADDING_VER && mouseY < PADDING_VER + buttonHeight;

    int playerConsoleButBgColor = getButBgColor(playerConsoleHover, _mode == 0);
    int computerConsoleButBgColor = getButBgColor(computerConsoleHover, _mode == 1);

    // Draw button backgrounds
    drawRect(PADDING_HOR, PADDING_VER, PADDING_HOR + playerConsoleButtonWidth, PADDING_VER + buttonHeight, playerConsoleButBgColor);
    drawRect(PADDING_HOR + playerConsoleButtonWidth, PADDING_VER, PADDING_HOR + playerConsoleButtonWidth + computerConsoleButtonWidth, PADDING_VER + buttonHeight, computerConsoleButBgColor);

    _playerConsoleModeButton = new Rectangle(PADDING_HOR, PADDING_VER, playerConsoleButtonWidth, buttonHeight);
    _computerConsoleModeButton = new Rectangle(PADDING_HOR + playerConsoleButtonWidth, PADDING_VER, computerConsoleButtonWidth, buttonHeight);

    // Draw button texts
    fontRenderer.drawString(PLAYER_CONSOLE_TEXT, PADDING_HOR + BUTTON_PADDING_HOR, PADDING_VER + BUTTON_PADDING_VER, BUTTON_TEXT_COLOR);
    fontRenderer.drawString(COMPUTER_CONSOLE_TEXT, PADDING_HOR + BUTTON_PADDING_HOR + playerConsoleButtonWidth, PADDING_VER + BUTTON_PADDING_VER, BUTTON_TEXT_COLOR);

    GL11.glPushMatrix();
    try {
      GL11.glTranslatef(PADDING_HOR, PADDING_VER + BUTTON_PADDING_VER * 2 + FONT_HEIGHT, 0);
      if (_mode == 0)
        drawPlayerConsole(timeSinceLastTick);
      else if (_mode == 1)
        drawComputerConsole(timeSinceLastTick);
    } finally {
      GL11.glPopMatrix();
    }
  }

  private void drawPlayerConsole(float timeSinceLastTick) {
    if (_editingProgram) {
      drawEditProgramConsole(timeSinceLastTick);
    } else {
      drawPlayerCommandConsole(timeSinceLastTick);
    }
  }

  private void drawPlayerCommandConsole(float timeSinceLastTick) {
    final String[] consoleLines = _playerConsole.getLines();
    // Draw all lines but first (we need to fill current edited line at the bottom)
    for (int i = 1; i < consoleLines.length; i++)
      drawMonospacedLine(consoleLines[i], 0, (i - 1) * FONT_HEIGHT, PLAYER_CONSOLE_TEXT_COLOR);

    String wholeCommandLine = ">" + _currentCommand.toString();
    String commandLine = wholeCommandLine.substring(_currentCommandDisplayStartIndex, Math.min(_currentCommandDisplayStartIndex + ComputerConsole.CONSOLE_WIDTH, wholeCommandLine.length()));
    int cursorPositionInDisplayedCommandLine = 1 + _cursorPositionInPlayerCommand - _currentCommandDisplayStartIndex;

    final int lastLineY = FONT_HEIGHT * (ComputerConsole.CONSOLE_HEIGHT - 1);
    drawMonospacedLine(commandLine, 0, lastLineY, PLAYER_CONSOLE_TEXT_COLOR);

    _blinkTick = ((++_blinkTick) % BLINK_LENGTH);
    if (_blinkTick * 2 > BLINK_LENGTH)
      drawVerticalLine(cursorPositionInDisplayedCommandLine * _characterWidth - 1, 1 + lastLineY, lastLineY + FONT_HEIGHT, PLAYER_CONSOLE_CURSOR_COLOR);
  }

  private void drawEditProgramConsole(float timeSinceLastTick) {
    for (int line = _editedDisplayStartY; line < Math.min(_editedProgramLines.size(), _editedDisplayStartY + ComputerConsole.CONSOLE_HEIGHT - 1); line++) {
      String programLine = _editedProgramLines.get(line).toString();
      if (programLine.length() > _editedDisplayStartX) {
        String displayedLine = programLine.substring(_editedDisplayStartX, Math.min(programLine.length(), _editedDisplayStartX + ComputerConsole.CONSOLE_WIDTH));
        drawMonospacedLine(displayedLine, 0, (line - _editedDisplayStartY) * FONT_HEIGHT, PROGRAM_TEXT_COLOR);
      }
    }

    final int lastLineY = FONT_HEIGHT * (ComputerConsole.CONSOLE_HEIGHT - 1);
    drawMonospacedLine("[S]ave E[x]it", 0, lastLineY, PROGRAM_LAST_LINE_COLOR);

    _blinkTick = ((++_blinkTick) % BLINK_LENGTH);
    if (_blinkTick * 2 > BLINK_LENGTH)
      drawVerticalLine((_editedProgramCursorX - _editedDisplayStartX) * _characterWidth - 1, 1 + (_editedProgramCursorY - _editedDisplayStartY) * FONT_HEIGHT, (_editedProgramCursorY - _editedDisplayStartY) * FONT_HEIGHT + FONT_HEIGHT, PROGRAM_CURSOR_COLOR);
  }

  private void drawComputerConsole(float timeSinceLastTick) {
    final String[] consoleLines = _computerConsole.getLines();
    for (int i = 0; i < consoleLines.length; i++)
      drawMonospacedLine(consoleLines[i], 0, i * FONT_HEIGHT, COMPUTER_CONSOLE_TEXT_COLOR);
  }

  private void drawMonospacedLine(String line, int x, int y, int color) {
    _renderEngine.bindTexture("/font/default.png");

    float blue = (float) (color >> 8 & 255) / 255.0F;
    float red = (float) (color >> 16 & 255) / 255.0F;
    float green = (float) (color & 255) / 255.0F;
    float alpha = (float) (color >> 24 & 255) / 255.0F;
    GL11.glColor4f(red, blue, green, alpha);

    // For some reason the text is actually drawn a bit higher than expected, so to correct it, I add 2 to "y"
    char[] chars = line.toCharArray();
    for (int i = 0; i < chars.length; i++)
      renderCharAt(chars[i], x + i * _characterWidth, y + 2);
  }

  private void renderCharAt(char ch, int x, int y) {
    float f = (float) (ch % 16 * 8);
    float f1 = (float) (ch / 16 * 8);
    float f3 = (float) _characterWidth;

    float toCenter = (_characterWidth - fontRenderer.getCharWidth(ch)) / 2f;
    GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
    GL11.glTexCoord2f(f / 128.0F, f1 / 128.0F);
    GL11.glVertex3f(x + toCenter, y, 0.0F);
    GL11.glTexCoord2f(f / 128.0F, (f1 + 7.99F) / 128.0F);
    GL11.glVertex3f(x + toCenter, y + 7.99F, 0.0F);
    GL11.glTexCoord2f((f + f3) / 128.0F, f1 / 128.0F);
    GL11.glVertex3f(x + toCenter + f3, y, 0.0F);
    GL11.glTexCoord2f((f + f3) / 128.0F, (f1 + 7.99F) / 128.0F);
    GL11.glVertex3f(x + toCenter + f3, y + 7.99F, 0.0F);
    GL11.glEnd();
  }

  @Override
  protected void mouseClicked(int mouseX, int mouseY, int which) {
    if (_mode == 0 && _computerConsoleModeButton.contains(mouseX - _screenX, mouseY - _screenY))
      _mode = 1;
    else if (_mode == 1 && _playerConsoleModeButton.contains(mouseX - _screenX, mouseY - _screenY))
      _mode = 0;
  }

  @Override
  protected void keyTyped(char character, int keyboardCharId) {
    if (_mode == 0) {
      if (_editingProgram) {
        keyTypedInEditingProgram(character, keyboardCharId);
      } else {
        keyTypedInPlayerConsole(character, keyboardCharId);
      }
    }
  }

  private void keyTypedInEditingProgram(char character, int keyboardCharId) {
    StringBuilder editedLine = _editedProgramLines.get(_editedProgramCursorY);
    if (character >= 32 && character < 127) {
      editedLine.insert(_editedProgramCursorX, character);
      _editedProgramCursorX++;
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
    } else if (keyboardCharId == Keyboard.KEY_DELETE) {
      if (_editedProgramCursorX < editedLine.length()) {
        editedLine.delete(_editedProgramCursorX, _editedProgramCursorX + 1);
      } else if (_editedProgramCursorY < _editedProgramLines.size() - 1) {
        editedLine.append(_editedProgramLines.get(_editedProgramCursorY + 1));
        _editedProgramLines.remove(_editedProgramCursorY + 1);
      }
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
    } else if (keyboardCharId == Keyboard.KEY_S && isCtrlKeyDown()) {
      StringBuilder program = new StringBuilder();
      for (int i = 0; i < _editedProgramLines.size(); i++) {
        if (i > 0)
          program.append("\n");
        program.append(_editedProgramLines.get(i));
      }
      try {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(baos);
        os.writeUTF(_editedProgramName);
        os.writeUTF(program.toString());
        PacketDispatcher.sendPacketToServer(new Packet250CustomPayload(Automation.SAVE_PROGRAM, baos.toByteArray()));
      } catch (IOException exp) {
        // TODO
      }
    }

    // Adjust cursor X position to be within the program line
    if (_editedProgramCursorX > _editedProgramLines.get(_editedProgramCursorY).length())
      _editedProgramCursorX = _editedProgramLines.get(_editedProgramCursorY).length();
  }

  private void keyTypedInPlayerConsole(char character, int keyboardCharId) {
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

      executeCommand(command);
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

  private void executeCommand(String command) {
    String[] commandParts = command.split(" ");
    if (commandParts.length > 0) {
      if (commandParts[0].equals("exit")) {
        this.mc.displayGuiScreen(null);
        this.mc.setIngameFocus();
      } else if (commandParts[0].equals("help")) {
        printHelp();
      } else if (commandParts[0].equals("edit")) {
        if (commandParts.length != 2) {
          _playerConsole.appendString("Usage:");
          _playerConsole.appendString("edit [programName] - edits or creates a new program with the specified name");
        } else if (!isValidProgramName(commandParts[1])) {
          _playerConsole.appendString("Invalid program name - only letters and digits allowed");
        } else {
          try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream os = new DataOutputStream(baos);
            os.writeUTF(commandParts[1]);
            PacketDispatcher.sendPacketToServer(new Packet250CustomPayload(Automation.DOWNLOAD_PROGRAM, baos.toByteArray()));

            _editingProgram = true;
            _editedProgramName = commandParts[1];
            _editedProgramLines = new ArrayList<StringBuilder>();
            _editedProgramLines.add(new StringBuilder());
            _editedProgramCursorX = 0;
            _editedProgramCursorY = 0;

          } catch (IOException exp) {
            // TODO
          }
        }
      } else {
        if (commandParts[0].length() > 0)
          _playerConsole.appendString("Unkown command - " + commandParts[0]);
      }
    }
  }

  private boolean isValidProgramName(String programName) {
    for (char c : programName.toCharArray()) {
      if (!Character.isDigit(c) && !Character.isLetter(c))
        return false;
    }
    return true;
  }

  private void printHelp() {
    _playerConsole.appendString("[put help text here]");
  }

  private int getButBgColor(boolean hover, boolean inMode) {
    if (hover)
      return BUTTON_BG_HOVER_COLOR;
    else if (inMode)
      return BUTTON_BG_ACTIVE_COLOR;
    else
      return BUTTON_BG_INACTIVE_COLOR;
  }

  public void clearConsole() {
    _computerConsole.clearConsole();
  }

  public void setConsoleState(String[] lines) {
    _computerConsole.setConsoleState(lines);
  }

  public void setCharacters(int x, int y, String text) {
    _computerConsole.setCharacters(x, y, text);
  }

  public void appendLines(String[] lines) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < lines.length; i++) {
      if (i > 0)
        result.append('\n');
      result.append(lines[i]);
    }

    _computerConsole.appendString(result.toString());
  }

  public void setProgramText(String programText) {
    _editedProgramLines = new ArrayList<StringBuilder>();
    for (String line : programText.split("\n"))
      _editedProgramLines.add(new StringBuilder(line));

    _editedProgramCursorX = 0;
    _editedProgramCursorY = 0;
  }
}
