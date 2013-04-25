package com.gempukku.minecraft.automation.computer.os;

import com.gempukku.minecraft.automation.computer.AbstractConditionCustomObject;
import com.gempukku.minecraft.automation.computer.JavaFunctionExecutable;
import com.gempukku.minecraft.automation.computer.ResultAwaitingCondition;
import com.gempukku.minecraft.automation.computer.ServerComputerData;
import com.gempukku.minecraft.automation.lang.ExecutionException;
import com.gempukku.minecraft.automation.lang.Variable;
import net.minecraft.world.World;

import java.util.Map;

public class CreateSleepTickFunction extends JavaFunctionExecutable {
	@Override
	protected int getDuration() {
		return 10;
	}

	@Override
	public String[] getParameterNames() {
		return new String[]{"ticks"};
	}

	@Override
	protected Object executeFunction(int line, World world, ServerComputerData computer, Map<String, Variable> parameters) throws ExecutionException {
		final Variable ticksVar = parameters.get("ticks");
		if (ticksVar.getType() != Variable.Type.NUMBER)
			throw new ExecutionException(line, "Expected NUMBER in createSleepTick()");

		final int ticks = ((Number) ticksVar.getValue()).intValue();
		if (ticks <= 0)
			throw new ExecutionException(line, "Sleep ticks must be greater than 0");

		return new AbstractConditionCustomObject() {
			@Override
			public int getCreationDelay() {
				return 0;
			}

			@Override
			public ResultAwaitingCondition createAwaitingCondition() {
				return new TicksAwaitingCondition(ticks);
			}
		};
	}

	private static class TicksAwaitingCondition implements ResultAwaitingCondition {
		private int _ticks;

		private TicksAwaitingCondition(int ticks) {
			_ticks = ticks;
		}

		@Override
		public boolean isMet(int checkAttempt, World world, ServerComputerData computer) throws ExecutionException {
			_ticks--;
			return _ticks < 0;
		}

		@Override
		public Variable getReturnValue() {
			return new Variable(null);
		}
	}
}
