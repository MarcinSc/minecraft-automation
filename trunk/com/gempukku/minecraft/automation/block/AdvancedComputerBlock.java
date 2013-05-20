package com.gempukku.minecraft.automation.block;

public class AdvancedComputerBlock extends ComputerBlock {
    public AdvancedComputerBlock(int id, String computerType) {
        super(id, computerType, 3);
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
