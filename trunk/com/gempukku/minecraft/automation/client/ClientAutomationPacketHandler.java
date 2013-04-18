package com.gempukku.minecraft.automation.client;

import com.gempukku.minecraft.automation.Automation;
import com.gempukku.minecraft.automation.computer.ComputerConsole;
import com.gempukku.minecraft.automation.gui.computer.console.ComputerConsoleGui;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ClientAutomationPacketHandler implements IPacketHandler {
    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
        final String channel = packet.channel;
        if (channel.equals(Automation.UPDATE_COMPUTER_LABEL)) {
            DataInputStream is = new DataInputStream(new ByteArrayInputStream(packet.data));
            try {
                int compId = is.readInt();
                String label = is.readUTF();
                Automation.getClientProxy().getRegistry().updateComputerLabel(compId, label);
            } catch (IOException exp) {
                // Ignore
            }
        } else if (channel.equals(Automation.CLEAR_CONSOLE_SCREEN)) {
            ComputerConsoleGui consoleGui = getComputerConsoleSafely();
            if (consoleGui != null)
                consoleGui.clearConsole();
        } else if (channel.equals(Automation.SET_CONSOLE_STATE)) {
            final ComputerConsoleGui consoleGui = getComputerConsoleSafely();
            if (consoleGui != null) {
                try {
                    DataInputStream is = new DataInputStream(new ByteArrayInputStream(packet.data));
                    String[] console = new String[ComputerConsole.CONSOLE_HEIGHT];
                    for (int i = 0; i < console.length; i++)
                        console[i] = is.readUTF();

                    consoleGui.setConsoleState(console);
                } catch (IOException exp) {
                    // TODO
                }
            }
        } else if (channel.equals(Automation.SET_CHARACTERS_IN_CONSOLE)) {
            final ComputerConsoleGui consoleGui = getComputerConsoleSafely();
            if (consoleGui != null) {
                try {
                    DataInputStream is = new DataInputStream(new ByteArrayInputStream(packet.data));
                    int x = is.readInt();
                    int y = is.readInt();
                    String text = is.readUTF();

                    consoleGui.setCharacters(x, y, text);
                } catch (IOException exp) {
                    // TODO
                }
            }
        } else if (channel.equals(Automation.APPEND_LINES_TO_CONSOLE)) {
            final ComputerConsoleGui consoleGui = getComputerConsoleSafely();
            if (consoleGui != null) {
                try {
                    DataInputStream is = new DataInputStream(new ByteArrayInputStream(packet.data));
                    int linesCount = is.readInt();
                    String[] lines = new String[linesCount];
                    for (int i = 0; i < lines.length; i++)
                        lines[i] = is.readUTF();

                    consoleGui.appendLines(lines);
                } catch (IOException exp) {
                    // TODO
                }
            }
        }
    }

    private ComputerConsoleGui getComputerConsoleSafely() {
        if (Minecraft.getMinecraft().inGameHasFocus)
            return null;

        final GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
        ComputerConsoleGui consoleGui = null;
        if (currentScreen instanceof ComputerConsoleGui)
            consoleGui = (ComputerConsoleGui) currentScreen;
        return consoleGui;
    }
}
