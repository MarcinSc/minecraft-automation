package com.gempukku.minecraft.automation.computer;

public class ComputerSpec {
    public final String computerType;
    public final int speed;
    public final int maxStackSize;
    public final int memory;
    public final int moduleSlotCount;

    public ComputerSpec(String computerType, int maxStackSize, int memory, int speed, int moduleSlotCount) {
        this.computerType = computerType;
        this.maxStackSize = maxStackSize;
        this.memory = memory;
        this.speed = speed;
        this.moduleSlotCount = moduleSlotCount;
    }
}
