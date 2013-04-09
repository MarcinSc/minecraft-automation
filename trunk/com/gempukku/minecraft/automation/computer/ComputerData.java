package com.gempukku.minecraft.automation.computer;

import com.gempukku.minecraft.automation.module.ComputerModule;

import java.util.HashMap;
import java.util.Map;

public class ComputerData {
    private String _label;
    private int[] _location = new int[3];
    private int _facing;
    private int _id;
    private String _owner;
    private Map<String, Map<String, String>> _moduleData =
            new HashMap<String, Map<String, String>>();

    public ComputerData(int id, String owner) {
        _id = id;
        _owner = owner;
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

    public int getModuleSlotCount() {
        return 10;
    }

    public ComputerModule getModuleAt(int slot) {
        return null;
    }

    public int getSpeed() {
        return 100;
    }

    public Map<String, String> getModuleData(String moduleType) {
        Map<String, String> moduleData = _moduleData.get(moduleType);
        if (moduleData == null) {
            moduleData = new HashMap<String, String>();
            _moduleData.put(moduleType, moduleData);
        }
        return moduleData;
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
