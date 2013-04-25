package com.gempukku.minecraft.automation.computer;

import com.gempukku.minecraft.automation.lang.Variable;
import com.gempukku.minecraft.automation.program.AwaitingCondition;

public interface ResultAwaitingCondition extends AwaitingCondition {
	public Variable getReturnValue();
}
