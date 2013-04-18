package com.gempukku.minecraft.automation.computer;

import com.gempukku.minecraft.automation.Automation;

public class ServerComputerData {
    private ComputerConsole _console = new ComputerConsole();
    private String _label;
    private String _computerType;
    private int[] _location = new int[3];
    private int _facing;
    private int _id;
    private String _owner;

    public ServerComputerData(int id, String owner, String computerType) {
        _id = id;
        _owner = owner;
        _computerType = computerType;
    }

    public ComputerConsole getConsole() {
        return _console;
    }

    public int getId() {
        return _id;
    }

    public String getLabel() {
        return _label;
    }

    public void setLabel(String label) {
        _label = label;
    }

    public String getOwner() {
        return _owner;
    }

    public String getComputerType() {
        return _computerType;
    }

    public int getSpeed() {
        return Automation.getServerProxy().getRegistry().getComputerSpeed(_computerType);
    }

    public int getMaxStackSize() {
        return Automation.getServerProxy().getRegistry().getComputerMaxStackSize(_computerType);
    }

    public int getMaxMemory() {
        return Automation.getServerProxy().getRegistry().getComputerMaxMemory(_computerType);
    }

    public void appendToConsole(String text) {
        _console.appendString(text);
    }

    public void setLocation(int x, int y, int z) {
        _location = new int[]{x, y, z};
    }

    public void setFacing(int facing) {
        _facing = facing;
    }

    public int getFacing() {
        return _facing;
    }

    public int getX() {
        return _location[0];
    }

    public int getY() {
        return _location[1];
    }

    public int getZ() {
        return _location[2];
    }
}
