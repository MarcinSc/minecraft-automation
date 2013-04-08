package com.gempukku.minecraft.automation;

import com.gempukku.minecraft.automation.computer.ComputerData;
import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.network.packet.Packet250CustomPayload;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClientAutomationRegistry implements AutomationRegistry {
    private Map<Integer, String> _computerLabels = new HashMap<Integer, String>();
    private Set<Integer> _pendingComputerLabels = new HashSet<Integer>();

    @Override
    public int assignNextComputerId() {
        throw new UnsupportedOperationException("Client cannot assign computer id");
    }

    @Override
    public ComputerData getComputerData(int computerId) {
        throw new UnsupportedOperationException("Client cannot get ComputerData");
    }

    @Override
    public String getComputerLabel(int computerId) {
        String result = _computerLabels.get(computerId);
        if (result == null) {
            if (!_pendingComputerLabels.contains(computerId)) {
                _pendingComputerLabels.add(computerId);
                requestComputerLabel(computerId);
            }
        }
        return result;
    }

    private void requestComputerLabel(int computerId) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(baos);
        try {
            os.writeInt(computerId);
            PacketDispatcher.sendPacketToServer(new Packet250CustomPayload(Automation.UPDATE_COMPUTER_LABEL, baos.toByteArray()));
        } catch (IOException exp) {
            // Ignore
        }
    }

    public void updateComputerLabel(int computerId, String label) {
        _pendingComputerLabels.remove(computerId);
        _computerLabels.put(computerId, label);
    }
}
