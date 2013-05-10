package com.gempukku.minecraft.automation.server;

import com.gempukku.minecraft.automation.Automation;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.gui.computer.console.ComputerConsoleContainerOnServer;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

import java.io.*;

public class ServerAutomationPacketHandler implements IPacketHandler {
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		final String channel = packet.channel;
		if (channel.equals(Automation.UPDATE_COMPUTER_LABEL)) {
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
		} else if (channel.equals(Automation.DOWNLOAD_PROGRAM)) {
			ComputerConsoleContainerOnServer container = getComputerConsoleContainerSafely(player);
			if (container != null) {
				DataInputStream is = new DataInputStream(new ByteArrayInputStream(packet.data));
				try {
					String programName = is.readUTF();
					String programText = Automation.getServerProxy().getComputerProcessing().getProgram(container.getComputerData().getId(), programName);
					if (programText != null) {
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						DataOutputStream os = new DataOutputStream(baos);
						os.writeUTF(programText);
						PacketDispatcher.sendPacketToPlayer(new Packet250CustomPayload(Automation.PROGRAM_TEXT, baos.toByteArray()), player);
					}
				} catch (IOException exp) {
					// Ignore
				}
			}
		} else if (channel.equals(Automation.SAVE_PROGRAM)) {
			ComputerConsoleContainerOnServer container = getComputerConsoleContainerSafely(player);
			if (container != null) {
				DataInputStream is = new DataInputStream(new ByteArrayInputStream(packet.data));
				try {
					String programName = is.readUTF();
					String programText = is.readUTF();
					Automation.getServerProxy().getComputerProcessing().saveProgram(container.getComputerData().getId(), programName, programText);
				} catch (IOException exp) {
					// Ignore
				}
			}
		} else if (channel.equals(Automation.INIT_CONSOLE)) {
			final ComputerConsoleContainerOnServer container = getComputerConsoleContainerSafely(player);
			if (container != null)
				container.initConsole();
		} else if (channel.equals(Automation.EXECUTE_PROGRAM)) {
			ComputerConsoleContainerOnServer container = getComputerConsoleContainerSafely(player);
			if (container != null) {
				DataInputStream is = new DataInputStream(new ByteArrayInputStream(packet.data));
				try {
					String programName = is.readUTF();
					final ServerComputerData computerData = container.getComputerData();
					String executeResult = Automation.getServerProxy().getComputerProcessing().startProgram(computerData.getId(), programName);
					if (executeResult == null)
						executeResult = "Program " + programName + " executed successfully";
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					DataOutputStream os = new DataOutputStream(baos);
					os.writeUTF(executeResult);
					PacketDispatcher.sendPacketToPlayer(new Packet250CustomPayload(Automation.DISPLAY_EXECUTION_RESULT, baos.toByteArray()), player);
				} catch (IOException exp) {
					// Ignore
				}
			}
		}
	}

	private ComputerConsoleContainerOnServer getComputerConsoleContainerSafely(Player player) {
		EntityPlayerMP playerMP = (EntityPlayerMP) player;
		Container container = playerMP.openContainer;
		if (container instanceof ComputerConsoleContainerOnServer)
			return (ComputerConsoleContainerOnServer) container;
		return null;
	}
}
