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

public class ComputerConsoleGui extends GuiScreen {
    public static final int BACKGROUND_COLOR = 0xff111111;
    public static final int FRAME_COLOR = 0xffffffff;
    public static final int BUTTON_TEXT_COLOR = 0xffffffff;
    public static final int BUTTON_BG_HOVER_COLOR = 0xff7f7f7f;
    public static final int BUTTON_BG_ACTIVE_COLOR = 0xffbfbfbf;
    public static final int BUTTON_BG_INACTIVE_COLOR = 0xff3f3f3f;

    public static final int COMPUTER_CONSOLE_TEXT_COLOR = 0xffffffff;

    private static final int PADDING_HOR = 5;
    private static final int PADDING_VER = 5;
    private static final int BUTTON_PADDING_HOR = 3;
    private static final int BUTTON_PADDING_VER = 3;

    public static final int FONT_HEIGHT = 9;
    public static final int CHARACTER_WIDTH = 6;
    public static final String PLAYER_CONSOLE_TEXT = "Player console";
    public static final String COMPUTER_CONSOLE_TEXT = "Computer console";

    // mode=0 is user (player) console, mode=1 is program output console
    private int _mode = 0;
    private boolean _editingProgram;

    private int _screenX;
    private int _screenY;
    private int _screenWidth;
    private int _screenHeight;

    private int _computerScreenWidth;
    private int _computerScreenHeight;

    private ComputerConsole _computerConsole = new ComputerConsole();
    private EntityPlayer _player;
    private ComputerTileEntity _tileEntity;

    private RenderEngine _renderEngine;

    private Rectangle _playerConsoleModeButton;
    private Rectangle _computerConsoleModeButton;

    private PlayerCommandConsoleGui _playerCommandConsoleGui;
    private ProgramEditingConsoleGui _programEditingConsoleGui;

    public ComputerConsoleGui(EntityPlayer player, ComputerTileEntity tileEntity) {
        _player = player;
        _tileEntity = tileEntity;
        _renderEngine = Minecraft.getMinecraft().renderEngine;
        _playerCommandConsoleGui = new PlayerCommandConsoleGui(this);
        _playerCommandConsoleGui.appendToConsole("AutomationOS v. 0.0");
        _playerCommandConsoleGui.appendToConsole("You're logged in as " + _player.getEntityName() + ", use \"exit\" command to exit the console, use \"help\" to list commands.");
        _programEditingConsoleGui = new ProgramEditingConsoleGui(this);

        PacketDispatcher.sendPacketToServer(new Packet250CustomPayload(Automation.INIT_CONSOLE, new byte[0]));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        _computerScreenWidth = CHARACTER_WIDTH * ComputerConsole.CONSOLE_WIDTH;
        _computerScreenHeight = FONT_HEIGHT * ComputerConsole.CONSOLE_HEIGHT;
        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    public void appendToPlayerConsole(String text) {
        _playerCommandConsoleGui.appendToConsole(text);
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
            _programEditingConsoleGui.drawEditProgramConsole(timeSinceLastTick, ComputerConsole.CONSOLE_WIDTH * CHARACTER_WIDTH, ComputerConsole.CONSOLE_HEIGHT * FONT_HEIGHT);
        } else {
            _playerCommandConsoleGui.drawPlayerCommandConsole(timeSinceLastTick);
        }
    }

    private void drawComputerConsole(float timeSinceLastTick) {
        final String[] consoleLines = _computerConsole.getLines();
        for (int i = 0; i < consoleLines.length; i++)
            drawMonospacedText(consoleLines[i], 0, i * FONT_HEIGHT, COMPUTER_CONSOLE_TEXT_COLOR);
    }

    @Override
    protected void drawVerticalLine(int par1, int par2, int par3, int par4) {
        super.drawVerticalLine(par1, par2, par3, par4);
    }

    @Override
    protected void drawHorizontalLine(int par1, int par2, int par3, int par4) {
        super.drawHorizontalLine(par1, par2, par3, par4);
    }

    protected void drawMonospacedText(String text, int x, int y, int color) {
        _renderEngine.bindTexture("/font/default.png");

        float blue = (float) (color >> 8 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color & 255) / 255.0F;
        float alpha = (float) (color >> 24 & 255) / 255.0F;
        GL11.glColor4f(red, blue, green, alpha);

        // For some reason the text is actually drawn a bit higher than expected, so to correct it, I add 2 to "y"
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++)
            renderCharAt(chars[i], x + i * CHARACTER_WIDTH, y + 2);
    }

    private void renderCharAt(char ch, int x, int y) {
        float f = (float) (ch % 16 * 8);
        float f1 = (float) (ch / 16 * 8);

        float toCenter = (CHARACTER_WIDTH - fontRenderer.getCharWidth(ch)) / 2f;
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
        GL11.glTexCoord2f(f / 128.0F, f1 / 128.0F);
        GL11.glVertex3f(x + toCenter, y, 0.0F);
        GL11.glTexCoord2f(f / 128.0F, (f1 + 7.99F) / 128.0F);
        GL11.glVertex3f(x + toCenter, y + 7.99F, 0.0F);
        GL11.glTexCoord2f((f + CHARACTER_WIDTH) / 128.0F, f1 / 128.0F);
        GL11.glVertex3f(x + toCenter + CHARACTER_WIDTH, y, 0.0F);
        GL11.glTexCoord2f((f + CHARACTER_WIDTH) / 128.0F, (f1 + 7.99F) / 128.0F);
        GL11.glVertex3f(x + toCenter + CHARACTER_WIDTH, y + 7.99F, 0.0F);
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
                _programEditingConsoleGui.keyTypedInEditingProgram(character, keyboardCharId);
            } else {
                _playerCommandConsoleGui.keyTypedInPlayerConsole(character, keyboardCharId);
            }
        }
    }

    protected void exitProgramming() {
        _editingProgram = false;
    }

    protected void executeCommand(String command) {
        String[] commandParts = command.split(" ");
        if (commandParts.length > 0) {
            if (commandParts[0].equals("exit")) {
                this.mc.displayGuiScreen(null);
                this.mc.setIngameFocus();
            } else if (commandParts[0].equals("help")) {
                printHelp();
            } else if (commandParts[0].equals("edit")) {
                if (commandParts.length != 2) {
                    _playerCommandConsoleGui.appendToConsole("Usage:");
                    _playerCommandConsoleGui.appendToConsole("edit [programName] - edits or creates a new program with the specified name");
                } else if (!isValidProgramName(commandParts[1])) {
                    _playerCommandConsoleGui.appendToConsole("Invalid program name - only letters and digits allowed");
                } else {
                    try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        DataOutputStream os = new DataOutputStream(baos);
                        os.writeUTF(commandParts[1]);
                        PacketDispatcher.sendPacketToServer(new Packet250CustomPayload(Automation.DOWNLOAD_PROGRAM, baos.toByteArray()));

                        _editingProgram = true;
                        _programEditingConsoleGui.reset(commandParts[1]);

                    } catch (IOException exp) {
                        // TODO
                    }
                }
            } else if (commandParts[0].equals("execute")) {
                if (commandParts.length != 2) {
                    _playerCommandConsoleGui.appendToConsole("Usage:");
                    _playerCommandConsoleGui.appendToConsole("execute [programName] - executes specified program");
                } else if (!isValidProgramName(commandParts[1]))
                    _playerCommandConsoleGui.appendToConsole("Invalid program name - only letters and digits allowed");
                else {
                    try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        DataOutputStream os = new DataOutputStream(baos);
                        os.writeUTF(commandParts[1]);
                        PacketDispatcher.sendPacketToServer(new Packet250CustomPayload(Automation.EXECUTE_PROGRAM, baos.toByteArray()));
                    } catch (IOException exp) {
                        // TODO
                    }
                }
            } else if (commandParts[0].equals("list")) {
                if (commandParts.length > 1) {
                    _playerCommandConsoleGui.appendToConsole("Usage:");
                    _playerCommandConsoleGui.appendToConsole("list - lists all programs on that computer");
                } else {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    PacketDispatcher.sendPacketToServer(new Packet250CustomPayload(Automation.LIST_PROGRAMS, baos.toByteArray()));
                }
            } else {
                if (commandParts[0].length() > 0)
                    _playerCommandConsoleGui.appendToConsole("Unknown command - " + commandParts[0]);
            }
        }
    }

    private boolean isValidProgramName(String programName) {
        if (programName.length() > 10 || programName.length() == 0)
            return false;
        for (char c : programName.toCharArray()) {
            if (!Character.isDigit(c) && !Character.isLetter(c))
                return false;
        }
        return true;
    }

    private void printHelp() {
        _playerCommandConsoleGui.appendToConsole("help - prints this text");
        _playerCommandConsoleGui.appendToConsole("edit [programName] - edits a program in an editor");
        _playerCommandConsoleGui.appendToConsole("execute [programName] - executes a program");
        _playerCommandConsoleGui.appendToConsole("list - lists all programs on that computer");
        _playerCommandConsoleGui.appendToConsole("exit - exits this console");
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
        _programEditingConsoleGui.setProgramText(programText);
    }
}
