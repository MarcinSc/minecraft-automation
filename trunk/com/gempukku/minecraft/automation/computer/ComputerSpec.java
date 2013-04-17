package com.gempukku.minecraft.automation.computer;

public class ComputerSpec {
    public final String computerType;
    public final int speed;
    public final int maxStackSize;
    public final int memory;

    public ComputerSpec(String computerType, int maxStackSize, int memory, int speed) {
        this.computerType = computerType;
        this.maxStackSize = maxStackSize;
        this.memory = memory;
        this.speed = speed;
    }
}
