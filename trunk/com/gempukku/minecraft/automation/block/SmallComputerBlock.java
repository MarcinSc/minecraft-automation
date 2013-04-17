package com.gempukku.minecraft.automation.block;

import com.gempukku.minecraft.automation.computer.ComputerSpec;

public class SmallComputerBlock extends ComputerBlock {
    public SmallComputerBlock(int id, ComputerSpec computerSpec) {
        super(id, computerSpec);
    }

    @Override
    protected String getComputerFrontReadyIcon() {
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
