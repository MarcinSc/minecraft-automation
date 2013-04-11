package com.gempukku.minecraft.automation;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

import java.io.*;

public class ServerAutomationPacketHandler implements IPacketHandler {
    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
        if (packet.channel.equals(Automation.UPDATE_COMPUTER_LABEL)) {
            DataInputStream is = new DataInputStream(new ByteArrayInputStream(packet.data));
            try {
                int compId = is.readInt();
                final String label = Automation.getServerProxy().getRegistry().getComputerLabel(compId);
                if (label != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    DataOutputStream os = new DataOutputStream(baos);
                    os.writeInt(compId);
                    os.writeUTF(label);
                    PacketDispatcher.sendPacketToPlayer(new Packet250CustomPayload(Automation.UPDATE_COMPUTER_LABEL, baos.toByteArray()), player);
                }
            } catch (IOException exp) {
                // Ignore
            }
        } else if (packet.channel.equals(Automation.CLIENT_INIT)) {
            Automation.getServerProxy();
        }
    }
}
