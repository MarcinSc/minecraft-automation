package com.gempukku.minecraft.automation.block;

public class PersonalComputerBlock extends ComputerBlock {
	public PersonalComputerBlock(int id, String computerType) {
		super(id, computerType, 1);
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
