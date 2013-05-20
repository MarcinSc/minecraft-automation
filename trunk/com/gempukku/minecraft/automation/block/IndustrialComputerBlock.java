package com.gempukku.minecraft.automation.block;

public class IndustrialComputerBlock extends ComputerBlock {
    public IndustrialComputerBlock(int id, String computerType) {
        super(id, computerType, 4);
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
