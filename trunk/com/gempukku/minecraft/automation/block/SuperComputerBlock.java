package com.gempukku.minecraft.automation.block;

public class SuperComputerBlock extends ComputerBlock {
    public SuperComputerBlock(int id, String computerType) {
        super(id, computerType, 5);
    }

    @Override
    public String getComputerFrontReadyIcon() {
        return "automation:computerFrontReady";
    }

    @Override
    protected String getComputerFrontWorkingIcon() {
        return "automation:computerFrontWorking";
    }

    @Override
    protected String getComputerSideIcon() {
        return "automation:computerSide";
    }
}
