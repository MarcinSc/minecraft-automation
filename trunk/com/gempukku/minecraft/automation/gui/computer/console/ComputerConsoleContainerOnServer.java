package com.gempukku.minecraft.automation.gui.computer.console;

import com.gempukku.minecraft.automation.Automation;
import com.gempukku.minecraft.automation.block.ComputerTileEntity;
import com.gempukku.minecraft.automation.computer.ComputerConsoleListener;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.network.packet.Packet250CustomPayload;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ComputerConsoleContainerOnServer extends Container implements ComputerConsoleListener {
	private EntityPlayer _player;
	private ServerComputerData _computerData;

	public ComputerConsoleContainerOnServer(EntityPlayer player, ComputerTileEntity computerTileEntity) {
		_player = player;
		_computerData = Automation.getServerProxy().getRegistry().getComputerData(computerTileEntity.getComputerId());
	}

	public ServerComputerData getComputerData() {
		return _computerData;
	}

	public void initConsole() {
		_computerData.getConsole().addConsoleListener(this);
	}

	@Override
	public void onCraftGuiClosed(EntityPlayer par1EntityPlayer) {
		_computerData.getConsole().removeConsoleListener(this);
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public void clearScreen() {
		PacketDispatcher.sendPacketToPlayer(new Packet250CustomPayload(Automation.CLEAR_CONSOLE_SCREEN, new byte[0]), (Player) _player);
	}

	@Override
	public void setScreenState(String[] screen) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream os = new DataOutputStream(baos);
			for (String s : screen)
				os.writeUTF(s);
			PacketDispatcher.sendPacketToPlayer(new Packet250CustomPayload(Automation.SET_CONSOLE_STATE, baos.toByteArray()), (Player) _player);
		} catch (IOException exp) {
			// Can't happen, we're writing to ByteArrayOutputStream
		}
	}

	@Override
	public void setCharactersStartingAt(int x, int y, String chars) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream os = new DataOutputStream(baos);
			os.writeInt(x);
			os.writeInt(y);
			os.writeUTF(chars);
			PacketDispatcher.sendPacketToPlayer(new Packet250CustomPayload(Automation.SET_CHARACTERS_IN_CONSOLE, baos.toByteArray()), (Player) _player);
		} catch (IOException exp) {
			// Can't happen, we're writing to ByteArrayOutputStream
		}
	}

	@Override
	public void appendLines(String[] lines) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream os = new DataOutputStream(baos);
			os.writeInt(lines.length);
			for (String line : lines)
				os.writeUTF(line);
			PacketDispatcher.sendPacketToPlayer(new Packet250CustomPayload(Automation.APPEND_LINES_TO_CONSOLE, baos.toByteArray()), (Player) _player);
		} catch (IOException exp) {
			// Can't happen, we're writing to ByteArrayOutputStream
		}
	}
}
