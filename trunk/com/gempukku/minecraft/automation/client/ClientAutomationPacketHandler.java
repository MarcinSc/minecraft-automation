package com.gempukku.minecraft.automation.client;

import com.gempukku.minecraft.automation.Automation;
import com.gempukku.minecraft.automation.gui.computer.console.ComputerConsoleGui;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.src.ModLoader;

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
            final GuiScreen currentScreen = ModLoader.getMinecraftInstance().currentScreen;
            if (currentScreen instanceof ComputerConsoleGui)
                ((ComputerConsoleGui) currentScreen).clearConsole();
        }
    }
}
