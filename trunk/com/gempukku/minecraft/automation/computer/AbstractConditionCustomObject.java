package com.gempukku.minecraft.automation.computer;

import com.gempukku.minecraft.automation.computer.os.ResultAwaitingCondition;
import com.gempukku.minecraft.automation.lang.CustomObject;

public abstract class AbstractConditionCustomObject implements CustomObject {
	@Override
	public String getType() {
		return "CONDITION";
	}

	public abstract int getCreationDelay();

	public abstract ResultAwaitingCondition createAwaitingCondition();
}
