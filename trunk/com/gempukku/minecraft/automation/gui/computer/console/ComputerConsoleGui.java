package com.gempukku.minecraft.automation.gui.computer.console;

import com.gempukku.minecraft.automation.computer.ComputerConsole;
import net.minecraft.client.gui.GuiScreen;

public class ComputerConsoleGui extends GuiScreen {
    private int _mode;
    private ComputerConsole _consoleOnClient = new ComputerConsole();

    public void clearConsole() {
        _consoleOnClient.clearConsole();
    }

    public void setConsoleState(String[] lines) {
        _consoleOnClient.setConsoleState(lines);
    }

    public void setCharacters(int x, int y, String text) {
        _consoleOnClient.setCharacters(x, y, text);
    }

    public void appendLines(String[] lines) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            if (i > 0)
                result.append('\n');
            result.append(lines[i]);
        }

        _consoleOnClient.appendString(result.toString());
    }
}
