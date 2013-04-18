package com.gempukku.minecraft.automation.computer;

public interface ComputerConsoleListener {
    /**
     * Clears the screen.
     */
    public void clearScreen();

    /**
     * Sets the state of the console screen to display the following lines.
     *
     * @param screen
     */
    public void setScreenState(String[] screen);

    /**
     * Replaces characters at the specified x,y
     *
     * @param x
     * @param y
     * @param chars
     */
    public void setCharactersStartingAt(int x, int y, String chars);

    /**
     * Appends the following lines to the console, moving anything that is on screen already to the top as many lines,
     * as many lines are getting appended.
     *
     * @param lines
     */
    public void appendLines(String[] lines);
}
