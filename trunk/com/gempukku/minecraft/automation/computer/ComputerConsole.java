package com.gempukku.minecraft.automation.computer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ComputerConsole {
    private Set<ComputerConsoleListener> _consoleListeners = new HashSet<ComputerConsoleListener>();
    private static final int _consoleWidth = 80;
    private static final int _consoleHeight = 40;

    // Please note, it's addressable by _chars[y][x] to allow easy creation of Strings based on line index
    private char[][] _chars = new char[_consoleHeight][_consoleWidth];

    public void addConsoleListener(ComputerConsoleListener listener) {
        _consoleListeners.add(listener);
        // Send the state immediately to the client
        String[] screen = new String[_consoleHeight];
        for (int i = 0; i < screen.length; i++)
            screen[i] = new String(_chars[i]);
        listener.setScreenState(screen);
    }

    public void removeConsoleListener(ComputerConsoleListener listener) {
        _consoleListeners.remove(listener);
    }

    public void appendString(String toAppend) {
        final String[] lines = toAppend.split("\n");
        List<String> realLinesToAppend = new ArrayList<String>();
        for (String line : lines) {
            final String printableLine = stripInvalidCharacters(line);
            for (int i = 0; i < printableLine.length(); i += _consoleWidth)
                realLinesToAppend.add(printableLine.substring(i, Math.min(i + _consoleWidth, printableLine.length())));
        }
        // Strip all the lines that are overflowing the screen
        if (realLinesToAppend.size() > _consoleHeight)
            realLinesToAppend = realLinesToAppend.subList(realLinesToAppend.size() - _consoleHeight, realLinesToAppend.size());

        String[] realLines = realLinesToAppend.toArray(new String[realLinesToAppend.size()]);

        // Move all existing lines up, unless we need to replace all lines
        if (realLines.length < _consoleHeight)
            System.arraycopy(_chars, realLines.length, _chars, 0, _consoleHeight - realLines.length);

        // Replace the lines (at the end) with the contents of realLines
        int startIndex = _consoleHeight - realLines.length;
        for (int i = startIndex; i < _consoleHeight; i++)
            _chars[i] = realLines[i - startIndex].toCharArray();

        // Notify listeners
        for (ComputerConsoleListener consoleListener : _consoleListeners)
            consoleListener.appendLines(realLines);
    }

    private String stripInvalidCharacters(String text) {
        StringBuilder result = new StringBuilder();
        final char[] chars = text.toCharArray();
        for (char aChar : chars)
            if (aChar >= 32 && aChar <= 126)
                result.append(aChar);

        return result.toString();
    }
}
