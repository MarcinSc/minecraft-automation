package com.gempukku.minecraft.automation;

import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.network.packet.Packet250CustomPayload;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class ClientAutomationProxy implements AutomationProxy {
    private ClientAutomationRegistry _automationRegistry;

    @Override
    public void initialize(File modConfigDirectory) {
        _automationRegistry = new ClientAutomationRegistry();
        sendClientInitEventToServer();
    }

    private void sendClientInitEventToServer() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PacketDispatcher.sendPacketToServer(new Packet250CustomPayload(Automation.CLIENT_INIT, baos.toByteArray()));
    }

    @Override
    public ClientAutomationRegistry getRegistry() {
        return _automationRegistry;
    }
}
