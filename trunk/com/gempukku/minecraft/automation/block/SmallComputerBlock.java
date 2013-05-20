package com.gempukku.minecraft.automation.block;

public class SmallComputerBlock extends ComputerBlock {
    public SmallComputerBlock(int id, String computerType) {
        super(id, computerType, 2);
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
