package com.gempukku.minecraft.automation.program;

import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import net.minecraft.world.World;

public interface AwaitingCondition {
	public boolean isMet(int checkAttempt, World world, ServerComputerData computer) throws ExecutionException;
}
