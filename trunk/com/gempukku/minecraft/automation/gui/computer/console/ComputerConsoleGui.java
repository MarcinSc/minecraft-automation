package com.gempukku.minecraft.automation.gui.computer.console;

import com.gempukku.minecraft.automation.computer.ComputerConsole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ComputerConsoleGui extends GuiScreen {
    public static final int BACKGROUND_COLOR = 0xff111111;
    public static final int FRAME_COLOR = 0xffffffff;
    public static final int BUTTON_TEXT_COLOR = 0xffffffff;
    public static final int BUTTON_BG_HOVER_COLOR = 0xff7f7f7f;
    public static final int BUTTON_BG_ACTIVE_COLOR = 0xffbfbfbf;
    public static final int BUTTON_BG_INACTIVE_COLOR = 0xff3f3f3f;

    public static final int PLAYER_CONSOLE_TEXT_COLOR = 0xffffffff;
    public static final int COMPUTER_CONSOLE_TEXT_COLOR = 0xffffffff;

    private static final int PADDING_HOR = 5;
    private static final int PADDING_VER = 5;
    private static final int BUTTON_PADDING_HOR = 3;
    private static final int BUTTON_PADDING_VER = 3;

    private static final int BLINK_LENGTH = 20;
    public static final String PLAYER_CONSOLE_TEXT = "Player console";
    public static final String COMPUTER_CONSOLE_TEXT = "Computer console";

    // mode=0 is user (player) console, mode=1 is edit program mode, mode=2 is program output console
    private int _mode = 0;

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
    private int _characterWidth;

    private RenderEngine _renderEngine;

    private int _blinkTick;

    private Rectangle _playerConsoleModeButton;
    private Rectangle _computerConsoleModeButton;

    public ComputerConsoleGui(EntityPlayer player) {
        _player = player;
        for (int i = 0; i < 20; i++)
            _playerConsole.appendString("Test line lkjfnvklesnfv shfvjsehbfvkjhs fvhjebsf vhbsefjvh eskjfbvj sehbfvjesb fjv");
        _renderEngine = Minecraft.getMinecraft().renderEngine;
        _playerConsole.appendString("AutomationOS v. 0.0");
        _playerConsole.appendString("Welcome " + _player.getEntityName());
        _currentCommand = new StringBuilder();
    }

    @Override
    public void initGui() {
        _characterWidth = 6;
        _computerScreenWidth = _characterWidth * ComputerConsole.CONSOLE_WIDTH;
        _computerScreenHeight = fontRenderer.FONT_HEIGHT * ComputerConsole.CONSOLE_HEIGHT;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float timeSinceLastTick) {
        // Draw the overlay on the world view (gradient)
        drawDefaultBackground();

        _screenWidth = _computerScreenWidth + PADDING_HOR * 2;
        _screenHeight = _computerScreenHeight + PADDING_VER * 2 + BUTTON_PADDING_VER * 2 + fontRenderer.FONT_HEIGHT;

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

        int buttonHeight = BUTTON_PADDING_VER * 2 + fontRenderer.FONT_HEIGHT;

        int playerConsoleButtonWidth = fontRenderer.getStringWidth(PLAYER_CONSOLE_TEXT) + BUTTON_PADDING_HOR * 2;
        int computerConsoleButtonWidth = fontRenderer.getStringWidth(COMPUTER_CONSOLE_TEXT) + BUTTON_PADDING_HOR * 2;

        boolean playerConsoleHover = mouseX >= PADDING_HOR && mouseX < PADDING_HOR + playerConsoleButtonWidth
                && mouseY >= PADDING_VER && mouseY < PADDING_VER + buttonHeight;
        boolean computerConsoleHover = mouseX >= PADDING_HOR + playerConsoleButtonWidth && mouseX < PADDING_HOR + playerConsoleButtonWidth + computerConsoleButtonWidth
                && mouseY >= PADDING_VER && mouseY < PADDING_VER + buttonHeight;

        int playerConsoleButBgColor = getButBgColor(playerConsoleHover, (_mode == 0 || _mode == 1));
        int computerConsoleButBgColor = getButBgColor(computerConsoleHover, (_mode == 2));

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
            GL11.glTranslatef(PADDING_HOR, PADDING_VER + BUTTON_PADDING_VER * 2 + fontRenderer.FONT_HEIGHT, 0);
            if (_mode == 0)
                drawPlayerConsole(timeSinceLastTick);
            else if (_mode == 1)
                drawEditProgramConsole(timeSinceLastTick);
            else if (_mode == 2)
                drawComputerConsole(timeSinceLastTick);
        } finally {
            GL11.glPopMatrix();
        }
    }

    private void drawPlayerConsole(float timeSinceLastTick) {
        final String[] consoleLines = _playerConsole.getLines();
        // Draw all lines but first (we need to fill current edited line at the bottom)
        for (int i = 1; i < consoleLines.length; i++)
            drawMonospacedLine(consoleLines[i], 0, (i - 1) * fontRenderer.FONT_HEIGHT, PLAYER_CONSOLE_TEXT_COLOR);

        String wholeCommandLine = ">" + _currentCommand.toString();
        String commandLine = wholeCommandLine.substring(_currentCommandDisplayStartIndex, Math.min(_currentCommandDisplayStartIndex + ComputerConsole.CONSOLE_WIDTH, wholeCommandLine.length()));
        int cursorPositionInDisplayedCommandLine = 1 + _cursorPositionInPlayerCommand + _currentCommandDisplayStartIndex;

        final int lastLineY = fontRenderer.FONT_HEIGHT * (ComputerConsole.CONSOLE_HEIGHT - 1);
        drawMonospacedLine(commandLine, 0, lastLineY, PLAYER_CONSOLE_TEXT_COLOR);

        _blinkTick = ((++_blinkTick) % BLINK_LENGTH);
        if (_blinkTick * 2 > BLINK_LENGTH)
            drawVerticalLine(1+cursorPositionInDisplayedCommandLine * _characterWidth, lastLineY, lastLineY + fontRenderer.FONT_HEIGHT, PLAYER_CONSOLE_TEXT_COLOR);
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

    private void drawEditProgramConsole(float timeSinceLastTick) {

    }

    private void drawComputerConsole(float timeSinceLastTick) {
        final String[] consoleLines = _computerConsole.getLines();
        for (int i = 0; i < consoleLines.length; i++)
            drawMonospacedLine(consoleLines[i], 0, i * fontRenderer.FONT_HEIGHT, COMPUTER_CONSOLE_TEXT_COLOR);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int which) {
        if ((_mode == 0 || _mode == 1) && _computerConsoleModeButton.contains(mouseX - _screenX, mouseY - _screenY))
            _mode = 2;
        else if (_mode == 2 && _playerConsoleModeButton.contains(mouseX - _screenX, mouseY - _screenY))
            _mode = 0;
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

}
