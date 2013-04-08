package com.gempukku.minecraft.automation;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ClientAutomationPacketHandler implements IPacketHandler {
    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
        if (packet.channel.equals(Automation.UPDATE_COMPUTER_LABEL)) {
            DataInputStream is = new DataInputStream(new ByteArrayInputStream(packet.data));
            try {
                int compId = is.readInt();
                String label = is.readUTF();
                ((ClientAutomationRegistry) Automation.getRegistry()).updateComputerLabel(compId, label);
            } catch (IOException exp) {
                // Ignore
            }
        }
    }
}
