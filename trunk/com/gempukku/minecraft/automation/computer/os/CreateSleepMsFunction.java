package com.gempukku.minecraft.automation.computer.os;

import com.gempukku.minecraft.automation.computer.*;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.world.World;

import java.util.Map;

public class CreateSleepMsFunction extends JavaFunctionExecutable {
	@Override
	protected int getDuration() {
		return 10;
	}

	@Override
	public String[] getParameterNames() {
		return new String[]{"time"};
	}

	@Override
	protected Object executeFunction(int line, World world, ComputerCallback computer, Map<String, Variable> parameters) throws ExecutionException {
		final Variable timeVar = parameters.get("time");
		if (timeVar.getType() != Variable.Type.NUMBER)
			throw new ExecutionException(line, "Expected NUMBER in createSleepMs()");

		final long time = ((Number) timeVar.getValue()).longValue();
		if (time <= 0)
			throw new ExecutionException(line, "Sleep time must be greater than 0");

		return new AbstractConditionCustomObject() {
			@Override
			public int getCreationDelay() {
				return 0;
			}

			@Override
			public ResultAwaitingCondition createAwaitingCondition() {
				return new SystemTimeAwaitingCondition(System.currentTimeMillis() + time);
			}
		};
	}

	private static class SystemTimeAwaitingCondition implements ResultAwaitingCondition {
		private long _finishAt;

		private SystemTimeAwaitingCondition(long finishAt) {
			_finishAt = finishAt;
		}

		@Override
		public boolean isMet(int checkAttempt, World world, ServerComputerData computer) throws ExecutionException {
			return System.currentTimeMillis() >= _finishAt;
		}

		@Override
		public Variable getReturnValue() {
			return new Variable(null);
		}
	}
}
