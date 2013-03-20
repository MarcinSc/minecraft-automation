package com.gempukku.minecraft.automation.computer;

public class ComputerData {
    private final String _owner;
    private String _label;

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
}
