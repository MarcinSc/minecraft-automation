package com.gempukku.minecraft.automation.computer;

public class ComputerData {
    private final String _owner;
    private String _label;
    private int[] _location = new int[3];
    private int _facing;

    public ComputerData(String owner) {
        _owner = owner;
    }

    public String getOwner() {
        return _owner;
    }

    public String getLabel() {
        return _label;
    }

    public void setLabel(String label) {
        _label = label;
    }

    public int getModuleSlotCount() {
        return 10;
    }

    public ComputerModule getModuleAt(int slot) {
        return null;
    }

    public int getSpeed() {
        return 100;
    }

    public void appendToConsole(String text) {
        
    }

    public void setLocation(int x, int y, int z) {
        _location = new int[] {x, y, z};
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
