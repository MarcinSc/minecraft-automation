package com.gempukku.minecraft.automation.item;

public class SmallComputerItemBlock extends ComputerItemBlock {
    public SmallComputerItemBlock(int id) {
        super(id);
    }

    @Override
    protected String getComputerIconToRegister() {
        return "automation:computer";
    }
}
